package debas.com.beaconnotifier.display;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import debas.com.beaconnotifier.AsyncTaskDB;
import debas.com.beaconnotifier.BeaconDetectorManager;
import debas.com.beaconnotifier.BeaconNotifierApp;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.database.BeaconDataBase;
import debas.com.beaconnotifier.display.fragment.BeaconViewerFragment;
import debas.com.beaconnotifier.display.fragment.HistoryBeaconFragment;
import debas.com.beaconnotifier.display.fragment.PreferencesFragment;
import debas.com.beaconnotifier.model.BeaconItemDB;
import debas.com.beaconnotifier.utils.Constants;
import debas.com.beaconnotifier.utils.Utils;

/**
 * Created by debas on 18/10/14.
 */
public class MainActivity extends ActionBarActivity implements BeaconConsumer {

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
    private String TAG = "onBeacon";

    List<RelativeLayout> linearLayouts = new ArrayList<RelativeLayout>();

    PagerAdapter mBeaconPagerAdapter;

    ViewPager mViewPager;

    private BeaconDetectorManager mBeaconDetectorManager = new BeaconDetectorManager();

    private View mHeaderView;
    private View mToolbarView;
    private int mBaseTranslationY;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentList.add(new HistoryBeaconFragment());
        fragmentList.add(new BeaconViewerFragment());
        fragmentList.add(new PreferencesFragment());

        setContentView(R.layout.activity_main);

        setContentView(R.layout.preferences);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mHeaderView = findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = findViewById(R.id.toolbar);
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mPager);

        slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                propagateToolbarState(toolbarIsShown());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        propagateToolbarState(toolbarIsShown());

//        linearLayouts.add((RelativeLayout) findViewById(R.id.first));
//        linearLayouts.add((RelativeLayout) findViewById(R.id.middle));
//        linearLayouts.add((RelativeLayout) findViewById(R.id.last));
//
//        mBeaconPagerAdapter = new BeaconPagerAdapter(getSupportFragmentManager(), fragmentList);
//        mViewPager = (ViewPager) findViewById(R.id.pager);
//        mViewPager.setAdapter(mBeaconPagerAdapter);
//        mViewPager.setOffscreenPageLimit(linearLayouts.size());
//
//        mBeaconManager.bind(this);
//
//        setLayoutChangeFragment();
//
//        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                    @Override
//                    public void onPageScrolled(int i, float v, int i2) {
//
//                    }
//
//                    @Override
//                    public void onPageSelected(int position) {
//                        for (int j = 0; j < linearLayouts.size(); j++) {
//                            if (j == position)
//                                linearLayouts.get(j).setBackground(getResources().getDrawable(R.drawable.gradient_selected));
//                            else
//                                linearLayouts.get(j).setBackground(getResources().getDrawable(R.drawable.gradient_unselected));
//                        }
//                    }
//
//                    @Override
//                    public void onPageScrollStateChanged(int i) {
//
//                    }
//                });

        /* check if this is the first time run */
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        boolean firstTimeRun = sharedPreferences.getBoolean(Constants.FIRST_LAUNCHED, true);
        if (firstTimeRun) {
            if (Utils.checkInternetConnectivity(getApplicationContext())) {
                final BeaconDataBase beaconDataBase = BeaconDataBase.getInstance(getApplicationContext());

                beaconDataBase.updateDB(getApplicationContext(), sharedPreferences, new AsyncTaskDB.OnDBUpdated() {
                    @Override
                    public void onDBUpdated(boolean result, int nbElement) {
                        if (!result) {
                            Toast.makeText(MainActivity.this, "failed to update DB", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "success to update DB : new element " + nbElement, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Internet needed to update DB", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
            if (firstScroll) {
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return;
        }
        View view = fragment.getView();
        if (view == null) {
            return;
        }

        // ObservableXxxViews have same API
        // but currently they don't have any common interfaces.
        adjustToolbar(scrollState, view);
    }

    private void adjustToolbar(ScrollState scrollState, View view) {
        int toolbarHeight = mToolbarView.getHeight();
        final Scrollable scrollView = (Scrollable) view.findViewById(R.id.scroll);
        if (scrollView == null) {
            return;
        }
        int scrollY = scrollView.getCurrentScrollY();
        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else {
            // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
            if (toolbarIsShown() || toolbarIsHidden()) {
                // Toolbar is completely moved, so just keep its state
                // and propagate it to other pages
                propagateToolbarState(toolbarIsShown());
            } else {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideToolbar()
                showToolbar();
            }
        }
    }

    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItemAt(mPager.getCurrentItem());
    }

    private void propagateToolbarState(boolean isShown) {
        int toolbarHeight = mToolbarView.getHeight();

        // Set scrollY for the fragments that are not created yet
        mPagerAdapter.setScrollY(isShown ? 0 : toolbarHeight);

        // Set scrollY for the active fragments
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Skip current item
            if (i == mPager.getCurrentItem()) {
                continue;
            }

            // Skip destroyed or not created item
            Fragment f = mPagerAdapter.getItemAt(i);
            if (f == null) {
                continue;
            }

            View view = f.getView();
            if (view == null) {
                continue;
            }
            propagateToolbarState(isShown, view, toolbarHeight);
        }
    }

    private void propagateToolbarState(boolean isShown, View view, int toolbarHeight) {
        Scrollable scrollView = (Scrollable) view.findViewById(R.id.scroll);
        if (scrollView == null) {
            return;
        }
        if (isShown) {
            // Scroll up
            if (0 < scrollView.getCurrentScrollY()) {
                scrollView.scrollVerticallyTo(0);
            }
        } else {
            // Scroll down (to hide padding)
            if (scrollView.getCurrentScrollY() < toolbarHeight) {
                scrollView.scrollVerticallyTo(toolbarHeight);
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
        propagateToolbarState(true);
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
        }
        propagateToolbarState(false);
    }

    /**
     * This adapter provides two types of fragments as an example.
     * {@linkplain #createItem(int)} should be modified if you use this example for your app.
     */
    private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

        private static final String[] TITLES = new String[]{"Applepie", "Butter Cookie", "Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb", "Ice Cream Sandwich", "Jelly Bean", "KitKat", "Lollipop"};

        private int mScrollY;

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setScrollY(int scrollY) {
            mScrollY = scrollY;
        }

        @Override
        protected Fragment createItem(int position) {
            // Initialize fragments.
            // Please be sure to pass scroll position to each fragments using setArguments.
            Fragment f;
            final int pattern = position % 3;
            switch (pattern) {
//                case 0: {
//                    f = new ViewPagerTabScrollViewFragment();
//                    if (0 <= mScrollY) {
//                        Bundle args = new Bundle();
//                        args.putInt(ViewPagerTabScrollViewFragment.ARG_SCROLL_Y, mScrollY);
//                        f.setArguments(args);
//                    }
//                    break;
//                }
//                case 1: {
//                    f = new ViewPagerTabListViewFragment();
//                    if (0 < mScrollY) {
//                        Bundle args = new Bundle();
//                        args.putInt(ViewPagerTabListViewFragment.ARG_INITIAL_POSITION, 1);
//                        f.setArguments(args);
//                    }
//                    break;
//                }
//                case 2:
//                default: {
//                    f = new ViewPagerTabRecyclerViewFragment();
//                    if (0 < mScrollY) {
//                        Bundle args = new Bundle();
//                        args.putInt(ViewPagerTabRecyclerViewFragment.ARG_INITIAL_POSITION, 1);
//                        f.setArguments(args);
//                    }
//                    break;
//                }
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

    public void setLayoutChangeFragment() {

        linearLayouts.get(0).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
                linearLayouts.get(0).setBackground(getResources().getDrawable(R.drawable.gradient_selected));
                linearLayouts.get(1).setBackground(getResources().getDrawable(R.drawable.gradient_unselected));
                linearLayouts.get(2).setBackground(getResources().getDrawable(R.drawable.gradient_unselected));
            }
        });

        linearLayouts.get(1).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
                linearLayouts.get(0).setBackground(getResources().getDrawable(R.drawable.gradient_unselected));
                linearLayouts.get(1).setBackground(getResources().getDrawable(R.drawable.gradient_selected));
                linearLayouts.get(2).setBackground(getResources().getDrawable(R.drawable.gradient_unselected));
            }
        });
        linearLayouts.get(2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
                linearLayouts.get(0).setBackground(getResources().getDrawable(R.drawable.gradient_unselected));
                linearLayouts.get(1).setBackground(getResources().getDrawable(R.drawable.gradient_unselected));
                linearLayouts.get(2).setBackground(getResources().getDrawable(R.drawable.gradient_selected));
            }
        });

        linearLayouts.get(0).setBackground(getResources().getDrawable(R.drawable.gradient_selected));
        linearLayouts.get(1).setBackground(getResources().getDrawable(R.drawable.gradient_unselected));
        linearLayouts.get(2).setBackground(getResources().getDrawable(R.drawable.gradient_unselected));
    }

    @Override
    public void onResume() {
        Log.d(TAG, "resume");
        super.onResume();
        if (mBeaconManager.isBound(this)) {
            mBeaconManager.setBackgroundMode(false);
        }
        ((BeaconNotifierApp) getApplication()).setCreateNotif(false);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "Pause");

        super.onResume();
        if (mBeaconManager.isBound(this)) {
            mBeaconManager.setBackgroundMode(true);
        }
        ((BeaconNotifierApp) getApplication()).setCreateNotif(true);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "destroyed");
        super.onDestroy();
        mBeaconManager.unbind(this);
    }


    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                BeaconViewerFragment beaconViewer = (BeaconViewerFragment) fragmentList.get(1);

                List<BeaconItemDB> beaconItemDB = mBeaconDetectorManager.getCurrentBeaconsAround(beacons, Calendar.getInstance().getTimeInMillis());
                beaconViewer.updateBeaconList(beaconItemDB);
            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public class BeaconPagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = { "Beacon", "History", "Favorites"};
        private List<Fragment> fragmentList;

        public BeaconPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }
}