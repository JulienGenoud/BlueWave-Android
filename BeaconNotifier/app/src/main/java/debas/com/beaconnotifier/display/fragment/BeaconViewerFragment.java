package debas.com.beaconnotifier.display.fragment;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.SlidingTabLayout;
import debas.com.beaconnotifier.SlidingTabStrip;
import debas.com.beaconnotifier.display.DisplayBeaconAdapter;
import debas.com.beaconnotifier.model.BeaconItemSeen;
import debas.com.beaconnotifier.utils.SortBeacon;

public class BeaconViewerFragment extends BaseFragment {

    private ListView mListView = null;
    private DisplayBeaconAdapter mDisplayBeaconAdapter = null;
    private List<BeaconItemSeen> mBeaconArray = new ArrayList<>();
    private List<BeaconItemSeen> mBeaconTempArray = new ArrayList<>();
    private SortBeacon mSortBeacon = new SortBeacon();
    private ImageView myAnimation;
    private ImageView mVignetteImageView;
    private HashMap<AnimationVignette, Integer> mAnimationHashMap = new HashMap<>();
    private RelativeLayout beaconView;
    private LinearLayout beaconLay;


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
        beaconView = (RelativeLayout)rootView.findViewById(R.id.nobeacon);
        beaconLay = (LinearLayout)rootView.findViewById(R.id.beacons_lay);

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

        mListView = (ListView) rootView.findViewById(R.id.listView);

//        mDisplayBeaconAdapter = new DisplayBeaconAdapter(getActivity().getApplicationContext());
//        mListView.setAdapter(mDisplayBeaconAdapter);
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

        setRetainInstance(true);
    }

    public void updateBeaconList(final List<BeaconItemSeen> beacons) {
        mBeaconArray = new ArrayList<>(beacons);
        Collections.sort(mBeaconArray, mSortBeacon);

        Log.d("update", "beacon " + beacons.size());

        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    boolean change = true;

                    List<BeaconItemSeen> oldList = mBeaconTempArray;//mDisplayBeaconAdapter.getBeaconList();
                    if (mBeaconArray.size() == 0 && oldList.size() != 0) {
                        startAnimationVignette(AnimationVignette.COLLAPSE, "" + beacons.size());
                    }
                    else {
                        beaconView.setVisibility(View.GONE);
                        beaconLay.setVisibility(View.VISIBLE);

                        if (mBeaconArray.size() != oldList.size() && oldList.size() != 0) {
                            startAnimationVignette(AnimationVignette.BOUNCE, "" + beacons.size());
                        } else if (mBeaconArray.size() > 0 && oldList.size() == 0) {
                            startAnimationVignette(AnimationVignette.EXPAND, "" + beacons.size());
                        } else {
                            change = false;
                        }
                    }

                    if (mBeaconArray.size() == 0) {
                        beaconLay.setVisibility(View.GONE);
                        beaconView.setVisibility(View.VISIBLE);
                    }


                    if (beacons.size() != 0 || change) {

                        mBeaconTempArray = mBeaconArray;

                        for (int i = 0; i < 3; i++) {
                            int id1 = getResources().getIdentifier("beacon" + String.valueOf(i + 1), "id", activity.getPackageName());
                            (activity.findViewById(id1)).setVisibility(View.GONE);
                        }


                        for (int i = 0; i < mBeaconArray.size(); i++) {


                            int id1 = getResources().getIdentifier("view" + String.valueOf(i + 1) +  "_1", "id", activity.getPackageName());
                            int id2 = getResources().getIdentifier("view" + String.valueOf(i + 1) +  "_2", "id", activity.getPackageName());
                            int id3 = getResources().getIdentifier("image" + String.valueOf(i + 1), "id", activity.getPackageName());

                            int id4 = getResources().getIdentifier("beacon" + String.valueOf(i + 1), "id", activity.getPackageName());

                            (activity.findViewById(id4)).setVisibility(View.VISIBLE);
                            TextView beacon_Title = (TextView) activity.findViewById(id1);
                            TextView beacon_Distance = (TextView) activity.findViewById(id2);

                            Animation animFadein = AnimationUtils.loadAnimation(activity.getApplicationContext(),
                                    R.anim.bounce2);


                            activity.findViewById(id3).startAnimation(animFadein);

                            BeaconItemSeen beacon = mBeaconArray.get(i);

                            beacon_Title.setText(beacon.mMajor + " - " + beacon.mMinor);
                            beacon_Distance.setText(String.format("%.2f meters away", beacon.mDistance));
                        }
                   //    mDisplayBeaconAdapter.setBeaconList(mBeaconArray);
   //                     mDisplayBeaconAdapter.notifyDataSetChanged();
                    }
                }
          });
         }
    }

    @Override
    public void buildMenu(Menu menu) {

    }


}
