package debas.com.beaconnotifier;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by debas on 03/03/15.
 */

public class BroadCastBluetoothReceiver extends BroadcastReceiver {

    private List<BroadCastBluetoothListener> mBroadCastBluetoothListeners = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            for (BroadCastBluetoothListener b : mBroadCastBluetoothListeners) {
                b.onChange(state);
            }
        }
    }

    public void addBroadCastBluetoothListner(BroadCastBluetoothListener broadCastBluetoothListener) {
        mBroadCastBluetoothListeners.add(broadCastBluetoothListener);
    }

    public interface BroadCastBluetoothListener {
        public void onChange(int state);
    }
}
