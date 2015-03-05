package debas.com.beaconnotifier.display;

import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import debas.com.beaconnotifier.AsyncTaskDB;
import debas.com.beaconnotifier.BeaconDetectorManager;
import debas.com.beaconnotifier.BeaconNotifierApp;
import debas.com.beaconnotifier.BroadCastBluetoothReceiver;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.SlidingTabLayout;
import debas.com.beaconnotifier.database.BeaconDataBase;
import debas.com.beaconnotifier.display.fragment.BaseFragment;
import debas.com.beaconnotifier.display.fragment.BeaconViewerFragment;
import debas.com.beaconnotifier.display.fragment.HistoryBeaconFragment;
import debas.com.beaconnotifier.display.fragment.PreferencesFragment;
import debas.com.beaconnotifier.model.BeaconItemSeen;
import debas.com.beaconnotifier.preferences.PreferencesHelper;
import debas.com.beaconnotifier.utils.Utils;


/**
 * Created by debas on 18/10/14.
 */
public class MainActivity extends BaseActivity implements BeaconConsumer, ObservableScrollViewCallbacks, RangeNotifier {

    public static final String LAUNCH_PAGE = "launch_page";
    public static final int AROUND_PAGE = 1;
    public static final int PREFERENCE_PAGE = 2;
    public static final int HISTORY_PAGE = 0;

    private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
    private String TAG = "onBeacon";

    private BeaconDetectorManager mBeaconDetectorManager = BeaconDetectorManager.getInstance();

    private View mToolbarView;
    private TouchInterceptionFrameLayout mInterceptionLayout;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    private int mSlop;
    private boolean mScrolled;
    private ScrollState mLastScrollState;
    private Menu mOptionsMenu;
    private BroadCastBluetoothReceiver mReceiver;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mOptionsMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("menu", "" + getCurrentFragment());
        MenuItem menuItem = menu.findItem(R.id.filter_history);
        MenuItem notificationItem = menu.findItem(R.id.notification_menu);
        MenuItem bluetoothItem = menu.findItem(R.id.bluetooth_menu);
        notificationItem.setChecked(PreferencesHelper.getNoticationEnable(this));
        notificationItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                PreferencesHelper.setNoticationEnable(MainActivity.this, item.isChecked());
                return true;
            }
        });

        bluetoothItem.setChecked(Utils.getBluetoothState());
        bluetoothItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.changeBluetoothState(!item.isChecked());
                return true;
            }
        });

        if (menuItem != null) {
            menuItem.setVisible(false);
        }
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).buildMenu(menu);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewpagertab);

        mToolbarView = findViewById(R.id.toolbar);

        setSupportActionBar((Toolbar) mToolbarView);

        ((Toolbar) mToolbarView).inflateMenu(R.menu.main);
        ((Toolbar) mToolbarView).setTitle(getString(R.string.app_name).toUpperCase());
        ViewCompat.setElevation(findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(3);

        mPager.setCurrentItem(AROUND_PAGE);

        // Padding for ViewPager must be set outside the ViewPager itself
        // because with padding, EdgeEffect of ViewPager become strange.
        final int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        findViewById(R.id.pager_wrapper).setPadding(0, getActionBarSize() + tabHeight, 0, 0);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mPager);
        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("page", "selected page change to " + position);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Log.d("database", BeaconItemSeen.listAll(BeaconItemSeen.class).size() + "");

        ViewConfiguration vc = ViewConfiguration.get(this);
        mSlop = vc.getScaledTouchSlop();
        mInterceptionLayout = (TouchInterceptionFrameLayout) findViewById(R.id.container);
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);

        mBeaconManager.bind(this);


        mReceiver = Utils.getBroadCastBluetoothReceiver();
        mReceiver.addBroadCastBluetoothListner(new BroadCastBluetoothReceiver.BroadCastBluetoothListener() {
            @Override
            public void onChange(int state) {
                MenuItem bluetoothMenu = mOptionsMenu.findItem(R.id.bluetooth_menu);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        bluetoothMenu.setChecked(false);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        bluetoothMenu.setChecked(true);
                        break;
                }
            }
        });
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);


        /* check if this is the first time run */
        boolean firstTimeRun = PreferencesHelper.getFirstLaunch(this);
        if (firstTimeRun) {
            if (Utils.checkInternetConnectivity(getApplicationContext())) {
                BeaconDataBase beaconDataBase = BeaconDataBase.getInstance(getApplicationContext());

                beaconDataBase.updateDB(getApplicationContext(), new AsyncTaskDB.OnDBUpdated() {
                    @Override
                    public void onDBUpdated(boolean result, int nbElement) {
                        if (!result) {
                            Toast.makeText(MainActivity.this, "Failed to get beacon list, check your connection and retry", Toast.LENGTH_LONG).show();
                        } else {
                            PreferencesHelper.setFirstLaunch(MainActivity.this, false);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Failed to get beacon list, check your connection and retry", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("FROM_NOTIFICATION", false)) {
            mPager.setCurrentItem(intent.getIntExtra(LAUNCH_PAGE, AROUND_PAGE));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBeaconManager.isBound(this)) {
            mBeaconManager.setBackgroundMode(false);
            mBeaconManager.setRangeNotifier(this);
        }
        ((BeaconNotifierApp) getApplication()).setCreateNotif(false);
    }

    @Override
    public void onPause() {
        super.onResume();

        if (mBeaconManager.isBound(this)) {
            mBeaconManager.setBackgroundMode(true);
        }
        ((BeaconNotifierApp) getApplication()).setCreateNotif(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
        unregisterReceiver(mReceiver);
    }


    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(this);
        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (!mScrolled) {
            // This event can be used only when TouchInterceptionFrameLayout
            // doesn't handle the consecutive events.
            adjustToolbar(scrollState);
        }
    }

    private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener = new TouchInterceptionFrameLayout.TouchInterceptionListener() {
        @Override
        public boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffX, float diffY) {
            if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
                // Horizontal scroll is maybe handled by ViewPager
                return false;
            }

            Scrollable scrollable = getCurrentScrollable();
            if (scrollable == null) {
                mScrolled = false;
                return false;
            }

            // If interceptionLayout can move, it should intercept.
            // And once it begins to move, horizontal scroll shouldn't work any longer.
            int toolbarHeight = mToolbarView.getHeight();
            int translationY = (int) mInterceptionLayout.getTranslationY();
            boolean scrollingUp = 0 < diffY;
            boolean scrollingDown = diffY < 0;
            if (scrollingUp) {
                if (translationY < 0) {
                    mScrolled = true;
                    mLastScrollState = ScrollState.UP;
                    return true;
                }
            } else if (scrollingDown) {
                if (-toolbarHeight < translationY) {
                    mScrolled = true;
                    mLastScrollState = ScrollState.DOWN;
                    return true;
                }
            }
            mScrolled = false;
            return false;
        }

        @Override
        public void onDownMotionEvent(MotionEvent ev) {
        }

        @Override
        public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
            float translationY = ScrollUtils.getFloat(mInterceptionLayout.getTranslationY() + diffY, -mToolbarView.getHeight(), 0);
            mInterceptionLayout.setTranslationY(translationY);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
            lp.height = (int) ((translationY < 0 ? -translationY : translationY) + getScreenHeight());
            mInterceptionLayout.requestLayout();
        }

        @Override
        public void onUpOrCancelMotionEvent(MotionEvent ev) {
            mScrolled = false;
            adjustToolbar(mLastScrollState);
        }
    };

    private Scrollable getCurrentScrollable() {
        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return null;
        }
        View view = fragment.getView();
        if (view == null) {
            return null;
        }
        return (Scrollable) view.findViewById(R.id.scroll);
    }

    private void adjustToolbar(ScrollState scrollState) {
        int toolbarHeight = mToolbarView.getHeight();
        final Scrollable scrollable = getCurrentScrollable();
        if (scrollable == null) {
            return;
        }
        int scrollY = scrollable.getCurrentScrollY();
        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else if (!toolbarIsShown() && !toolbarIsHidden()) {
            // Toolbar is moving but doesn't know which to move:
            // you can change this to hideToolbar()
            showToolbar();
        }
    }

    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItemAt(mPager.getCurrentItem());
    }

    private boolean toolbarIsShown() {
        return mInterceptionLayout.getTranslationY() == 0;
    }

    private boolean toolbarIsHidden() {
        return mInterceptionLayout.getTranslationY() == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        animateToolbar(0);
    }

    private void hideToolbar() {
        animateToolbar(-mToolbarView.getHeight());
    }

    private void animateToolbar(final float toY) {
        float layoutTranslationY = mInterceptionLayout.getTranslationY();
        if (layoutTranslationY != toY) {
            ValueAnimator animator = ValueAnimator.ofFloat(mInterceptionLayout.getTranslationY(), toY).setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float translationY = (float) animation.getAnimatedValue();
                    mInterceptionLayout.setTranslationY(translationY);
                    if (translationY < 0) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
                        lp.height = (int) (-translationY + getScreenHeight());
                        mInterceptionLayout.requestLayout();
                    }
                }
            });
            animator.start();
        }
    }

    private List<BeaconItemSeen> oldBeacon = new ArrayList<>();

    @Override
    public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
        final HistoryBeaconFragment historyBeacon = (HistoryBeaconFragment) mPagerAdapter.getItemAt(HISTORY_PAGE);
        final BeaconViewerFragment aroundBeacons = (BeaconViewerFragment) mPagerAdapter.getItemAt(AROUND_PAGE);

        if (aroundBeacons == null || historyBeacon == null)
            return;
        mBeaconDetectorManager.getCurrentBeaconsAround(beacons, Calendar.getInstance().getTimeInMillis(),
                new BeaconDetectorManager.OnFinish() {
                    @Override
                    public void result(List<BeaconItemSeen> beaconItemAround) {
                        if (oldBeacon == null) {
                            oldBeacon = new ArrayList<>(beaconItemAround);
                        }

                        aroundBeacons.updateBeaconList(beaconItemAround);
                        if (!beaconItemAround.equals(oldBeacon)) {
                            Log.d("contains", "" + beaconItemAround.size());
                            historyBeacon.updateHistory();
                        }

                        oldBeacon = mBeaconDetectorManager.epurNewBeacons(oldBeacon, beaconItemAround);
                        for (BeaconItemSeen beaconItemSeen : beaconItemAround) {
                            Log.d("fav", "" + beaconItemSeen.mFavorites);
                            beaconItemSeen.mSeen = Calendar.getInstance().getTimeInMillis();
                            beaconItemSeen.save();
                        }
                    }
                });
    }

    /**
     * This adapter provides two types of fragments as an example.
     * {@linkplain #createItem(int)} should be modified if you use this example for your app.
     */
    private class NavigationAdapter extends CacheFragmentStatePagerAdapter {
        private final String[] TITLES = new String[]{getResources().getString(R.string.history),
                getResources().getString(R.string.proximity), getResources().getString(R.string.preferences)};

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final Object fragment = super.instantiateItem(container, position);
            try {
                final Field saveFragmentStateField = Fragment.class.getDeclaredField("mSavedFragmentState");
                saveFragmentStateField.setAccessible(true);
                final Bundle savedFragmentState = (Bundle) saveFragmentStateField.get(fragment);
                if (savedFragmentState != null) {
                    savedFragmentState.setClassLoader(Fragment.class.getClassLoader());
                }
            } catch (Exception e) {
                Log.w("CustomFragmentStatePagerAdapter", "Could not get mSavedFragmentState field: " + e);
            }
            return fragment;
        }

        @Override
        protected Fragment createItem(int position) {
            Fragment f;
            switch (position) {
                case 0:
                    f = new HistoryBeaconFragment();
                    break;
                case 1:
                    f = new BeaconViewerFragment();
                    break;
                case 2:
                    f = new PreferencesFragment();
                    break;
                default:
                    f = new PreferencesFragment();
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }
}