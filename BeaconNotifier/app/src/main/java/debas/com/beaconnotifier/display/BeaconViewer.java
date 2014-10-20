package debas.com.beaconnotifier.display;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import debas.com.beaconnotifier.BeaconNotifierApp;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.utils.SortBeacon;
import debas.com.beaconnotifier.utils.Constants;


public class BeaconViewer extends Fragment {

    private ListView mListView = null;
    private DisplayBeaconAdapter mDisplayBeaconAdapter = null;
    private List<Beacon> mBeaconArray = new ArrayList<Beacon>();
    private SortBeacon mSortBeacon = new SortBeacon();
    private String LIST_INSTANCE_STATE = "list_instance_state";

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.beacon_viewer, container, false);

//        try {
//            if (!mBeaconManager.checkAvailability()) {
//            /* bletooth disable */
//                LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.bluetooth_disabled);
//                ListView listView = (ListView) rootView.findViewById(R.id.listView);
//                linearLayout.setVisibility(LinearLayout.VISIBLE);
//                listView.setVisibility(ListView.GONE);
//
//            }
//        } catch (RuntimeException e) {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle("Bluetooth LE not available");
//            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
//            builder.setPositiveButton(android.R.string.ok, null);
//            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    getActivity().finish();
//                    System.exit(0);
//                }
//            });
//            builder.show();
//        }
//
        mListView = (ListView) rootView.findViewById(R.id.listView);
        mDisplayBeaconAdapter = new DisplayBeaconAdapter(getActivity().getApplicationContext());
        mListView.setAdapter(mDisplayBeaconAdapter);

        /* check if first run */
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        boolean firstTimeRun = sharedPreferences.getBoolean(Constants.FIRST_LAUNCHED, true);
        if (firstTimeRun) {
            Toast.makeText(getActivity(), "First run", Toast.LENGTH_LONG).show();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.FIRST_LAUNCHED, false).commit();
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Beacon", "created");

        /* database test */
//        BeaconDataBase beaconDataBase = ((BeaconNotifierApp) getApplication()).getBeaconDataBase();
//        beaconDataBase.open();
//        boolean result = beaconDataBase.isBeaconWithUUID("53168465-4534-6543-2134-546865413213");
//        if (result) {
//            Toast.makeText(this, "beacon founded", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(this, "beacon not founded", Toast.LENGTH_LONG).show();
//            beaconDataBase.insertBeacon();
//        }
//        beaconDataBase.close();
    }

//    @Override
//    public void onStart() {
//        Log.d("Beacon", "started");
//        super.onStart();
//        if (mBeaconManager.isBound(this)) {
//            mBeaconManager.setBackgroundMode(false);
//        }
//        ((BeaconNotifierApp) getActivity().getApplication()).setCreateNotif(false);
//    }
//
//    @Override
//    public void onResume() {
//        Log.d("Beacon", "resume");
//        super.onResume();
//        if (mBeaconManager.isBound(this)) {
//            mBeaconManager.setBackgroundMode(false);
//        }
//        ((BeaconNotifierApp) getActivity().getApplication()).setCreateNotif(false);
//    }
//
//    @Override
//    public void onPause() {
//        Log.d("Beacon", "Pause");
//
//        super.onResume();
//        if (mBeaconManager.isBound(this)) {
//            mBeaconManager.setBackgroundMode(true);
//        }
//        ((BeaconNotifierApp) getActivity().getApplication()).setCreateNotif(true);
//    }
//
//    @Override
//    public void onStop() {
//        Log.d("Beacon", "stoped");
//
//        super.onStop();
//        if (mBeaconManager.isBound(this)) {
//            mBeaconManager.setBackgroundMode(true);
//        }
//    ((BeaconNotifierApp) getActivity().getApplicationContext()).setCreateNotif(true);
//    }
//
//    @Override
//    public void onDestroy() {
//        Log.d("Beacon", "destroyed");
//        super.onDestroy();
//        mBeaconManager.unbind(this);
//    }


    public void updateBeaconList(Collection<Beacon> beacons) {
        mBeaconArray.clear();
        mBeaconArray.addAll(beacons);
        Collections.sort(mBeaconArray, mSortBeacon);

        Log.d("onBeacon", "test : " + beacons.size());

        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                    public void run() {
                        mDisplayBeaconAdapter.setBeaconList(mBeaconArray);
                        mDisplayBeaconAdapter.notifyDataSetChanged();
              }
          });
         }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_INSTANCE_STATE, mListView.onSaveInstanceState());
    }

//    @Override
//    public void onBeaconServiceConnect() {
//        mBeaconManager.setRangeNotifier(new RangeNotifier() {
//            @Override
//            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
//                mBeaconArray.clear();
//                mBeaconArray.addAll(beacons);
//                Collections.sort(mBeaconArray, mSortBeacon);
//
//                Log.d("onBeacon", "test : " + beacons.size());
//
//                Activity activity = getActivity();
////                if (activity != null) {
//                    activity.runOnUiThread(new Runnable() {
//                        public void run() {
//                            mDisplayBeaconAdapter.setBeaconList(mBeaconArray);
//                            mDisplayBeaconAdapter.notifyDataSetChanged();
//                        }
//                    });
////                }
//            }
//        });

//        try {
//            mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId",
//                    Identifier.parse("53168465-4534-6543-2134-546865413213"),
//                    Identifier.fromInt(10),
//                    Identifier.fromInt(1)));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public Context getApplicationContext() {
//        return getActivity().getApplicationContext();
//    }
//
//    @Override
//    public void unbindService(ServiceConnection serviceConnection) {
//        getActivity().getApplicationContext().unbindService(serviceConnection);
//    }
//
//    @Override
//    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
//        return getActivity().getApplicationContext().bindService(intent, serviceConnection, i);
//    }
}
