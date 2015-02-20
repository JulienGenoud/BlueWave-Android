package debas.com.beaconnotifier.display.fragment;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dexafree.materialList.view.MaterialStaggeredGridView;

import java.util.ArrayList;
import java.util.List;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.display.CustomBeaconCard;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 18/10/14.
 */
public class HistoryBeaconFragment extends Fragment implements View.OnClickListener {

    private Typeface mLight, mBold, mLightItalic;
    private TextView[] mFilterText = new TextView[3];
    private TextView currentSelection;
    private MaterialStaggeredGridView mMaterialGridView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history, container, false);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-CondLight.ttf");
        mBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-CondBold.ttf");
        mLightItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-CondLightItalic.ttf");

        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mFilterText[0] = (TextView) view.findViewById(R.id.history_all);
        mFilterText[1] = (TextView) view.findViewById(R.id.not_seen);
        mFilterText[2] = (TextView) view.findViewById(R.id.favorites);

        for (TextView textView : mFilterText) {
            textView.setTypeface(mLight);
            textView.setOnClickListener(this);
            textView.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        }

        mMaterialGridView = (MaterialStaggeredGridView) view.findViewById(R.id.gridview);

        List<BeaconItemSeen> beaconItemSeenList = BeaconItemSeen.findWithQuery(BeaconItemSeen.class, "select * from " + BeaconItemSeen.getTableName(BeaconItemSeen.class) + " order by m_seen asc");
        for (BeaconItemSeen beaconItemSeen : beaconItemSeenList) {
            mMaterialGridView.getAdapter().insert(new CustomBeaconCard(beaconItemSeen), 0);
        }

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

            int number = mMaterialGridView.getAdapter().getCount();
            final List<CustomBeaconCard> beaconToInsert = new ArrayList<>();

            @Override
            protected List<CustomBeaconCard> doInBackground(Void... params) {
                for (BeaconItemSeen beaconItemSeen : beaconItemAround) {

                    Log.d("testRemove", "test");

                    for (int i = 0; i < number; i++) {
                        CustomBeaconCard customBeaconCard = (CustomBeaconCard) mMaterialGridView.getCard(i);

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
                mMaterialGridView.getAdapter().remove(values[0]);
            }

            @Override
            protected void onPostExecute(List<CustomBeaconCard> customBeaconCards) {
                super.onPostExecute(customBeaconCards);

                for (CustomBeaconCard customBeaconCard : customBeaconCards) {
                    mMaterialGridView.getAdapter().insert(customBeaconCard, 0);
                }
                mMaterialGridView.notifyDataSetChanged();
            }
        }.execute();

    }
}
