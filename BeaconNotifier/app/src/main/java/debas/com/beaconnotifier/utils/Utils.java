package debas.com.beaconnotifier.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import debas.com.beaconnotifier.BroadCastBluetoothReceiver;
import debas.com.beaconnotifier.R;

/**
 * Created by debas on 21/10/14.
 */
public class Utils {

    private static BroadCastBluetoothReceiver mBroadCastBluetoothReceiver = new BroadCastBluetoothReceiver();

    public static boolean checkInternetConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        // only when connected or while connecting...
        if (netInfo != null && netInfo.isConnectedOrConnecting() &&
                ((netInfo.getType() == ConnectivityManager.TYPE_MOBILE) || (netInfo.getType() == ConnectivityManager.TYPE_WIFI))) {
            return true;
        } else {
            return false;
        }
    }

    public static void changeBluetoothState(boolean bool) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bool && !mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        if (!bool && mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    public static boolean getBluetoothState() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    public static BroadCastBluetoothReceiver getBroadCastBluetoothReceiver() {
        return mBroadCastBluetoothReceiver;
    }

    public static int getAssociatedImage(int major, int minor) {
        if (major == 36 && minor == 55)
            return R.drawable.eglise;
        else if (major == 65 && minor == 12)
            return R.drawable.tableau;
        return R.drawable.def;
    };
}
