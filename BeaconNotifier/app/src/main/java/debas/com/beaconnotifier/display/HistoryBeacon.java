package debas.com.beaconnotifier.display;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import debas.com.beaconnotifier.R;

/**
 * Created by debas on 18/10/14.
 */
public class HistoryBeacon extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history, container, false);

        return rootView;
    }
}
