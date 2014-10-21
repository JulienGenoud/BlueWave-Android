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
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;

import android.widget.Toast;

import debas.com.beaconnotifier.BeaconNotifierApp;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.database.AsyncTaskDB;
import debas.com.beaconnotifier.database.BeaconDataBase;
import debas.com.beaconnotifier.display.fragment.BeaconViewer;
import debas.com.beaconnotifier.display.fragment.FavoritesBeacons;
import debas.com.beaconnotifier.display.fragment.HistoryBeacon;
import debas.com.beaconnotifier.utils.Constants;
import debas.com.beaconnotifier.utils.Utils;

import com.astuetz.PagerSlidingTabStrip;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by debas on 18/10/14.
 */
public class MainActivity extends FragmentActivity implements BeaconConsumer {

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private BroadcastReceiver mReceiver;
    private BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this);
    private String TAG = "onBeacon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("save = ", (savedInstanceState == null ? "null" : "pas null"));
        fragmentList.add(new BeaconViewer());
        fragmentList.add(new HistoryBeacon());
        fragmentList.add(new FavoritesBeacons());


        System.out.println("onCreate");
        setContentView(R.layout.activity_main);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        pager.setAdapter(myPagerAdapter);
        pager.setOffscreenPageLimit(3);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setShouldExpand(true);
        tabs.setViewPager(pager);

        mBeaconManager.bind(this);

        BeaconManager.debug = true;

                /* check if this is the first time run */
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        boolean firstTimeRun = sharedPreferences.getBoolean(Constants.FIRST_LAUNCHED, true);
        if (firstTimeRun) {
            if (Utils.checkInternetConnectivity(getApplicationContext())) {
                final BeaconDataBase beaconDataBase = ((BeaconNotifierApp) getApplication()).getBeaconDataBase();

                beaconDataBase.open();
                beaconDataBase.updateDB(null, sharedPreferences, new AsyncTaskDB.OnDBUpdated() {
                    @Override
                    public void onDBUpdated(boolean result, int nbElement) {
                        if (!result) {
                            Toast.makeText(MainActivity.this, "failed to update DB", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "success to update DB : new element " + nbElement, Toast.LENGTH_LONG).show();
                        }
                        beaconDataBase.close();
                    }
                });
            } else {
                Toast.makeText(this, "Internet needed to update DB", Toast.LENGTH_LONG).show();
            }
        }

//        BeaconConsumer fragment = (BeaconConsumer) myPagerAdapter.getItem(0);
//        fragment.getApplicationContext();
//        mReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                final String action = intent.getAction();
//
//                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
//                            BluetoothAdapter.ERROR);
//                    LinearLayout linearLayout;
//                    ListView listView;
//                    switch (state) {
//                        case BluetoothAdapter.STATE_OFF:
////                            imageView = (ImageView) findViewById(R.id.bluetooth_disabled);
//                            listView = (ListView) findViewById(R.id.listView);
//                            linearLayout = (LinearLayout) findViewById(R.id.bluetooth_disabled);
////                            imageView.setVisibility(ImageView.VISIBLE);
//                            listView.setVisibility(ListView.GONE);
//                            linearLayout.setVisibility(LinearLayout.VISIBLE);
//                            Toast.makeText(MainActivity.this, "bluetooth turning off ...", Toast.LENGTH_LONG).show();
//                            break;
//                        case BluetoothAdapter.STATE_ON:
//                            linearLayout = (LinearLayout) findViewById(R.id.bluetooth_disabled);
//                            listView = (ListView) findViewById(R.id.listView);
//                            linearLayout.setVisibility(LinearLayout.GONE);
//                            listView.setVisibility(ListView.VISIBLE);
//                            Toast.makeText(MainActivity.this, "bluetooth turning on ...", Toast.LENGTH_LONG).show();
//                            break;
//                    }
//                }
//            }
//        };
//        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        registerReceiver(mReceiver, filter);

    }

//    @Override
//    public void onStart() {
//        Log.d(TAG, "started");
//        super.onStart();
//        if (mBeaconManager.isBound(this)) {
//            mBeaconManager.setBackgroundMode(false);
//        }
//        ((BeaconNotifierApp) getApplication()).setCreateNotif(false);
//    }
//
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

//    @Override
//    public void onStop() {
//        Log.d(TAG, "stoped");
//
//        super.onStop();
//        if (mBeaconManager.isBound(this)) {
//            mBeaconManager.setBackgroundMode(true);
//        }
//        ((BeaconNotifierApp) getApplicationContext()).setCreateNotif(true);
//    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();

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
                BeaconViewer beaconViewer = (BeaconViewer) fragmentList.get(0);

                beaconViewer.updateBeaconList(beacons);
            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId",
                    Identifier.parse("53168465-4534-6543-2134-546865413213"),
                    Identifier.fromInt(10),
                    Identifier.fromInt(1)));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = { "Beacon", "History", "Favorites"};
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

//    @Override
//    public void onBackPressed() {
//        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setTitle("Closing BeaconNotifier")
//                .setMessage("Are you sure you want to close ?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }
}