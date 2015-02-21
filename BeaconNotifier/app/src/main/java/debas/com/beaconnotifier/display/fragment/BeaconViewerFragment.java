package debas.com.beaconnotifier.display.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.display.DisplayBeaconAdapter;
import debas.com.beaconnotifier.model.BeaconItemSeen;
import debas.com.beaconnotifier.utils.Constants;
import debas.com.beaconnotifier.utils.SortBeacon;


public class BeaconViewerFragment extends BaseFragment {

    public static final String ARG_SCROLL_Y = "ARG_SCROLL_Y";

    private ListView mListView = null;
    private DisplayBeaconAdapter mDisplayBeaconAdapter = null;
    private List<BeaconItemSeen> mBeaconArray = new ArrayList<>();
    private SortBeacon mSortBeacon = new SortBeacon();
    private String LIST_INSTANCE_STATE = "list_instance_state";

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.beacon_viewer, container, false);

        final ObservableScrollView scrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll);
        Activity parentActivity = getActivity();
        scrollView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));
        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            // Scroll to the specified offset after layout
            scrollView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }

        mListView = (ListView) rootView.findViewById(R.id.listView);

        mDisplayBeaconAdapter = new DisplayBeaconAdapter(getActivity().getApplicationContext());
        mListView.setAdapter(mDisplayBeaconAdapter);

        /* check if first run */
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        boolean firstTimeRun = sharedPreferences.getBoolean(Constants.FIRST_LAUNCHED, true);
        if (firstTimeRun) {
            Toast.makeText(getActivity(), "First run", Toast.LENGTH_LONG).show();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.FIRST_LAUNCHED, false).apply();
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Beacon", "created");

        if (savedInstanceState != null) {
            Parcelable mListInstanceState = savedInstanceState.getParcelable(LIST_INSTANCE_STATE);
            mListView.onRestoreInstanceState(mListInstanceState);
        }

        setRetainInstance(true);
    }



    public void updateBeaconList(List<BeaconItemSeen> beacons) {
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

        Log.d("BeaconViewer", "saveinstance");
        outState.putParcelable(LIST_INSTANCE_STATE, mListView.onSaveInstanceState());
    }

}
