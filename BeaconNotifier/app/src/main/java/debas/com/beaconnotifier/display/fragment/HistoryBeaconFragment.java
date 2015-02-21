package debas.com.beaconnotifier.display.fragment;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import java.util.ArrayList;
import java.util.List;

import debas.com.beaconnotifier.MaterialObservableGridView;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.display.CustomBeaconCard;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 18/10/14.
 */
public class HistoryBeaconFragment extends BaseFragment implements View.OnClickListener {

    private Typeface mLight, mBold;
    private TextView currentSelection;
    private MaterialObservableGridView materialObservableGridView;

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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView[] mFilterText = new TextView[3];
        mFilterText[0] = (TextView) view.findViewById(R.id.history_all);
        mFilterText[1] = (TextView) view.findViewById(R.id.not_seen);
        mFilterText[2] = (TextView) view.findViewById(R.id.favorites);

        for (TextView textView : mFilterText) {
            textView.setTypeface(mLight);
            textView.setOnClickListener(this);
            textView.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        }

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
                    materialObservableGridView.getAdapter().insert(new CustomBeaconCard(beaconItemSeen), 0);
                }
            }
        }.execute();

        onClick(mFilterText[0]);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            TextView textView = (TextView) v;

            if (currentSelection != null) {
                currentSelection.setTypeface(mLight);
                currentSelection.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            }
            currentSelection = textView;
            currentSelection.setTypeface(mBold);
            currentSelection.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | currentSelection.getPaintFlags());

        }
    }

    public void updateHistory(final List<BeaconItemSeen> beaconItemAround) {

        new AsyncTask<Void, CustomBeaconCard, List<CustomBeaconCard>>() {

            int number = materialObservableGridView.getAdapter().getCount();
            final List<CustomBeaconCard> beaconToInsert = new ArrayList<>();

            @Override
            protected List<CustomBeaconCard> doInBackground(Void... params) {
                for (BeaconItemSeen beaconItemSeen : beaconItemAround) {

                    Log.d("testRemove", "test");

                    for (int i = 0; i < number; i++) {
                        CustomBeaconCard customBeaconCard = (CustomBeaconCard) materialObservableGridView.getCard(i);

                        if (customBeaconCard.getBeaconItemSeen().mBeaconId.equalsIgnoreCase(beaconItemSeen.mBeaconId)) {
                            publishProgress(customBeaconCard);
                        }
                    }

                    CustomBeaconCard customBeaconCard = new CustomBeaconCard(beaconItemSeen);
                    beaconToInsert.add(customBeaconCard);
                }
                return beaconToInsert;
            }

            @Override
            protected void onProgressUpdate(CustomBeaconCard... values) {
                super.onProgressUpdate(values);
                Log.d("removing", "removing id " + values[0].getBeaconItemSeen().mBeaconId);
                materialObservableGridView.getAdapter().remove(values[0]);
            }

            @Override
            protected void onPostExecute(List<CustomBeaconCard> customBeaconCards) {
                super.onPostExecute(customBeaconCards);

                for (CustomBeaconCard customBeaconCard : customBeaconCards) {
                    materialObservableGridView.getAdapter().insert(customBeaconCard, 0);
                }
                materialObservableGridView.notifyDataSetChanged();
            }
        }.execute();
    }
}
