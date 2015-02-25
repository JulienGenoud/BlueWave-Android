package debas.com.beaconnotifier.display.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
import java.util.HashMap;
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
    private ImageView mVignetteImageView;
    private HashMap<AnimationVignette, Integer> mAnimationHashMap = new HashMap<>();

    public static enum AnimationVignette {
        EXPAND, COLLAPSE, BOUNCE
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.beacon_viewer, container, false);
    }

    @Override
    public void onViewCreated(View rootView, @Nullable Bundle savedInstanceState) {

        myAnimation = (ImageView)rootView.findViewById(R.id.myanimation);
        myAnimation.setBackgroundResource(R.drawable.imageanim);
        AnimationDrawable frameAnimation = (AnimationDrawable) myAnimation.getBackground();
        frameAnimation.start();

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) getActivity().findViewById(R.id.sliding_tabs);
        SlidingTabStrip slidingTabStrip = slidingTabLayout.getTabStrip();
        RelativeLayout view = (RelativeLayout) slidingTabStrip.getChildAt(1);
        mVignetteImageView = (ImageView) view.findViewById(R.id.tab_around_beacon_count);

        mAnimationHashMap.put(AnimationVignette.EXPAND,  R.anim.zoom_in);
        mAnimationHashMap.put(AnimationVignette.BOUNCE,  R.anim.bounce);
        mAnimationHashMap.put(AnimationVignette.COLLAPSE,  R.anim.zoom_out);

//        startAnimationVignette(AnimationVignette.EXPAND, "0");

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
    }

    private void startAnimationVignette(AnimationVignette animationVignette, String text) {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), mAnimationHashMap.get(animationVignette));
        TextDrawable textDrawable = TextDrawable.builder()
                .beginConfig()
                .textColor(getResources().getColor(R.color.accent))
                .bold()
                .endConfig()
                .buildRound(text, getResources().getColor(R.color.title));
        mVignetteImageView.setImageDrawable(textDrawable);
//        Animation currentAnim = mVignetteImageView.getAnimation();
//        if (currentAnim != null && !currentAnim.hasEnded()) {
//            return;
//        }
        switch (animationVignette) {
            case EXPAND:
                mVignetteImageView.setVisibility(View.VISIBLE);
                break;
            case COLLAPSE:
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mVignetteImageView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
        }
        mVignetteImageView.startAnimation(animation);
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
        mBeaconArray = new ArrayList<>(beacons);
        Collections.sort(mBeaconArray, mSortBeacon);

        Log.d("update", "beacon " + beacons.size());

        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    boolean change = true;

                    List<BeaconItemSeen> oldList = mDisplayBeaconAdapter.getBeaconList();
                    if (mBeaconArray.size() == 0 && oldList.size() != 0) {
                        myAnimation.setVisibility(View.VISIBLE);
                        startAnimationVignette(AnimationVignette.COLLAPSE, "" + beacons.size());
                    }
                    else {
                        myAnimation.setVisibility(View.GONE);
                        if (mBeaconArray.size() != oldList.size() && oldList.size() != 0) {
                            startAnimationVignette(AnimationVignette.BOUNCE, "" + beacons.size());
                        } else if (mBeaconArray.size() > 0 && oldList.size() == 0) {
                            startAnimationVignette(AnimationVignette.EXPAND, "" + beacons.size());
                        } else {
                            change = false;
                        }
                    }

                    if (change) {
                        mDisplayBeaconAdapter.setBeaconList(mBeaconArray);
                        mDisplayBeaconAdapter.notifyDataSetChanged();
                    }
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

    @Override
    public void buildMenu(Menu menu) {

    }
}
