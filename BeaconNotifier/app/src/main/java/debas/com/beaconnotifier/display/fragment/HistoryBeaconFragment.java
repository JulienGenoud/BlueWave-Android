package debas.com.beaconnotifier.display.fragment;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.shamanland.fab.ShowHideOnScroll;

import java.util.ArrayList;
import java.util.List;

import debas.com.beaconnotifier.MaterialObservableGridView;
import debas.com.beaconnotifier.OnHistoryBeaconClickListener;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.display.BeaconHistoryCard;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 18/10/14.
 */
public class HistoryBeaconFragment extends BaseFragment {

    private Typeface mLight, mBold;
    private TextView currentSelection;
    private MaterialObservableGridView materialObservableGridView;
    private FloatingActionsMenu mFloatingActionsMenu;
    private OnHistoryBeaconClickListener mOnHistoryBeaconClickListener = new OnHistoryBeaconClickListener() {
        @Override
        public void onBeaconClick(View v, BeaconHistoryCard beaconHistoryCard) {
            if (v.getId() == R.id.favorites_heart) {
                ImageView favoritesView = (ImageView) v;

                BeaconItemSeen beaconItemSeen = beaconHistoryCard.getBeaconItemSeen();
                if (beaconItemSeen.mFavorites) {
                    favoritesView.setImageResource(R.drawable.favorites_empty);
                } else {
                    favoritesView.setImageResource(R.drawable.favorites_full);
                }
                beaconItemSeen.mFavorites = !beaconItemSeen.mFavorites;
                beaconItemSeen.save();
            }
            else {
                Toast.makeText(getActivity(), "should open !", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history, container, false);

        Activity parentActivity = getActivity();
        materialObservableGridView = (MaterialObservableGridView) view.findViewById(R.id.scroll);
        materialObservableGridView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));

        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            materialObservableGridView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-CondLight.ttf");
        mBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-CondBold.ttf");

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        TextView[] mFilterText = new TextView[3];
//        mFilterText[0] = (TextView) view.findViewById(R.id.history_all);
//        mFilterText[1] = (TextView) view.findViewById(R.id.not_seen);
//        mFilterText[2] = (TextView) view.findViewById(R.id.favorites);
//
//        for (TextView textView : mFilterText) {
//            textView.setTypeface(mLight);
//            textView.setOnClickListener(this);
//            textView.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
//        }

        materialObservableGridView = (MaterialObservableGridView) view.findViewById(R.id.scroll);
//        materialObservableGridView.setCardAnimation(IMaterialView.CardAnimation.SCALE_IN);

        new AsyncTask<Void, Void, List<BeaconItemSeen>>() {
            @Override
            protected List<BeaconItemSeen> doInBackground(Void... params) {
                return BeaconItemSeen.findWithQuery(BeaconItemSeen.class, "select * from " + BeaconItemSeen.getTableName(BeaconItemSeen.class) + " order by m_seen asc");
            }

            @Override
            protected void onPostExecute(List<BeaconItemSeen> beaconItemSeens) {

                for (BeaconItemSeen beaconItemSeen : beaconItemSeens) {
                    materialObservableGridView.getAdapter().insert(new BeaconHistoryCard(beaconItemSeen, mOnHistoryBeaconClickListener), 0);
                }
            }
        }.execute();

        mFloatingActionsMenu = (FloatingActionsMenu) view.findViewById(R.id.floating_history_menu);

        view.findViewById(R.id.floating_history_all).setOnClickListener(mFloatingButtonClickListener);
        view.findViewById(R.id.floating_history_notseen).setOnClickListener(mFloatingButtonClickListener);
        view.findViewById(R.id.floating_history_favorites).setOnClickListener(mFloatingButtonClickListener);

        materialObservableGridView.setOnTouchListener(new ShowHideOnScroll(mFloatingActionsMenu));
    }

    private View.OnClickListener mFloatingButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFloatingActionsMenu.collapse();
        }
    };

    public void updateHistory(final List<BeaconItemSeen> beaconItemAround) {

        new AsyncTask<Void, BeaconHistoryCard, List<BeaconHistoryCard>>() {

            int number = materialObservableGridView.getAdapter().getCount();
            final List<BeaconHistoryCard> beaconToInsert = new ArrayList<>();

            @Override
            protected List<BeaconHistoryCard> doInBackground(Void... params) {
                for (BeaconItemSeen beaconItemSeen : beaconItemAround) {

                    Log.d("testRemove", "test");

                    for (int i = 0; i < number; i++) {
                        BeaconHistoryCard beaconHistoryCard = (BeaconHistoryCard) materialObservableGridView.getCard(i);

                        if (beaconHistoryCard.getBeaconItemSeen().mBeaconId.equalsIgnoreCase(beaconItemSeen.mBeaconId)) {
                            publishProgress(beaconHistoryCard);
                        }
                    }

                    BeaconHistoryCard beaconHistoryCard = new BeaconHistoryCard(beaconItemSeen, mOnHistoryBeaconClickListener);
                    beaconToInsert.add(beaconHistoryCard);
                }
                return beaconToInsert;
            }

            @Override
            protected void onProgressUpdate(BeaconHistoryCard... values) {
                super.onProgressUpdate(values);
                Log.d("removing", "removing id " + values[0].getBeaconItemSeen().mBeaconId);
                materialObservableGridView.getAdapter().remove(values[0]);
            }

            @Override
            protected void onPostExecute(List<BeaconHistoryCard> beaconHistoryCards) {
                super.onPostExecute(beaconHistoryCards);

                for (BeaconHistoryCard beaconHistoryCard : beaconHistoryCards) {
                    materialObservableGridView.getAdapter().insert(beaconHistoryCard, 0);
                }
                materialObservableGridView.notifyDataSetChanged();
            }
        }.execute();
    }
}
