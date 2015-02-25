package debas.com.beaconnotifier.display.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.SlidingTabLayout;
import debas.com.beaconnotifier.SlidingTabStrip;
import debas.com.beaconnotifier.display.DisplayBeaconAdapter;
import debas.com.beaconnotifier.model.BeaconItemSeen;
import debas.com.beaconnotifier.utils.Constants;
import debas.com.beaconnotifier.utils.SortBeacon;

public class BeaconViewerFragment extends BaseFragment {

    private ListView mListView = null;
    private DisplayBeaconAdapter mDisplayBeaconAdapter = null;
    private List<BeaconItemSeen> mBeaconArray = new ArrayList<>();
    private SortBeacon mSortBeacon = new SortBeacon();
    private String LIST_INSTANCE_STATE = "list_instance_state";

    private ImageView myAnimation;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.beacon_viewer, container, false);

        myAnimation = (ImageView)rootView.findViewById(R.id.myanimation);
        myAnimation.setBackgroundResource(R.drawable.imageanim);
        AnimationDrawable frameAnimation = (AnimationDrawable) myAnimation.getBackground();
        frameAnimation.start();


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



    public void updateBeaconList(final List<BeaconItemSeen> beacons) {
        mBeaconArray.clear();
        mBeaconArray.addAll(beacons);
        Collections.sort(mBeaconArray, mSortBeacon);

        Log.d("onBeacon", "test : " + beacons.size());

        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (mBeaconArray.size() == 0) {
                            myAnimation.setVisibility(View.VISIBLE);
                        }
                        else {
                            myAnimation.setVisibility(View.GONE);
                            mDisplayBeaconAdapter.setBeaconList(mBeaconArray);
                            mDisplayBeaconAdapter.notifyDataSetChanged();
                        }
              }
          });
         }
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SlidingTabLayout slidingTabLayout = (SlidingTabLayout) activity.findViewById(R.id.sliding_tabs);
                SlidingTabStrip slidingTabStrip = slidingTabLayout.getTabStrip();
                RelativeLayout view = (RelativeLayout) slidingTabStrip.getChildAt(1);
                final ImageView imageView = (ImageView) view.findViewById(R.id.tab_around_beacon_count);

                if (!(imageView.getDrawable() instanceof TextDrawable)) {
                    TextDrawable textDrawable = TextDrawable.builder()
                            .beginConfig()
                            .textColor(getResources().getColor(R.color.accent))
                            .bold()
                            .endConfig()
                            .buildRound("0", getResources().getColor(R.color.title));

                    imageView.setImageDrawable(textDrawable);

                    Animation animZoomIn = AnimationUtils.loadAnimation(activity,
                            R.anim.launch_zoom_in);

                    imageView.setVisibility(View.VISIBLE);
                    imageView.startAnimation(animZoomIn);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("BeaconViewer", "saveinstance");
        outState.putParcelable(LIST_INSTANCE_STATE, mListView.onSaveInstanceState());
    }

    @Override
    public void buildMenu(Menu menu) {

    }
}
