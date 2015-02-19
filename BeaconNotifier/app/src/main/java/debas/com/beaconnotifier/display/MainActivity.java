package debas.com.beaconnotifier.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
import debas.com.beaconnotifier.display.fragment.BeaconViewer;
import debas.com.beaconnotifier.display.fragment.FavoritesBeacons;
import debas.com.beaconnotifier.display.fragment.HistoryBeacon;
import debas.com.beaconnotifier.model.BeaconItemDB;
import debas.com.beaconnotifier.utils.Constants;
import debas.com.beaconnotifier.utils.Utils;

/**
 * Created by debas on 18/10/14.
 */
public class MainActivity extends FragmentActivity implements BeaconConsumer {

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private BroadcastReceiver mReceiver;
    private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
    private String TAG = "onBeacon";

    List<RelativeLayout> linearLayouts = new ArrayList<RelativeLayout>();

    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

    ViewPager mViewPager;

    private BeaconDetectorManager mBeaconDetectorManager = new BeaconDetectorManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("save = ", (savedInstanceState == null ? "null" : "pas null"));
        fragmentList.add(new HistoryBeacon());
        fragmentList.add(new BeaconViewer());
        fragmentList.add(new FavoritesBeacons());

        System.out.println("onCreate");
        setContentView(R.layout.activity_main);

        linearLayouts.add((RelativeLayout)findViewById(R.id.first));
        linearLayouts.add((RelativeLayout)findViewById(R.id.middle));
        linearLayouts.add((RelativeLayout)findViewById(R.id.last));

        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mBeaconManager.bind(this);


        linearLayouts.get(0).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
                linearLayouts.get(0).setBackground(getResources().getDrawable(R.drawable.gradiant));
                linearLayouts.get(1).setBackground(getResources().getDrawable(R.drawable.transparant));
                linearLayouts.get(2).setBackground(getResources().getDrawable(R.drawable.transparant));
            }
        });

        linearLayouts.get(1).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
                linearLayouts.get(0).setBackground(getResources().getDrawable(R.drawable.transparant));
                linearLayouts.get(1).setBackground(getResources().getDrawable(R.drawable.gradiant));
                linearLayouts.get(2).setBackground(getResources().getDrawable(R.drawable.transparant));
            }
        });
        linearLayouts.get(2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
                linearLayouts.get(0).setBackground(getResources().getDrawable(R.drawable.transparant));
                linearLayouts.get(1).setBackground(getResources().getDrawable(R.drawable.transparant));
                linearLayouts.get(2).setBackground(getResources().getDrawable(R.drawable.gradiant));
            }
        });

        mViewPager.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageScrolled(int i, float v, int i2) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                       // linearLayouts.get(position).setBackground(getDrawable(R.drawable.transparant));

                        for (int j = 0; j < linearLayouts.size(); j++) {
                            if (j == position)
                                linearLayouts.get(j).setBackground(getResources().getDrawable(R.drawable.gradiant));
                            else
                                linearLayouts.get(j).setBackground(getResources().getDrawable(R.drawable.transparant));
                        }

                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });


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
                Log.d("MainActivity", "didrangebeaconsregion : " + beacons.size());
                BeaconViewer beaconViewer = (BeaconViewer) fragmentList.get(1);

                Log.d("time", Calendar.getInstance().getTimeInMillis() + "");
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


    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public Fragment getItem(int i)
        {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }




    public class MyPagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = { "Beacon", "History", "Favorites"};
        private final int[] COLORS = { R.color.black, R.color.dark_bluewave, R.color.red_bluewave};

        private List<Fragment> fragmentList;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
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