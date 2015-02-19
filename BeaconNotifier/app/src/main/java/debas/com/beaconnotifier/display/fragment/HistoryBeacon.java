package debas.com.beaconnotifier.display.fragment;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dexafree.materialList.view.MaterialStaggeredGridView;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.display.CustomBeaconCard;

/**
 * Created by debas on 18/10/14.
 */
public class HistoryBeacon extends Fragment implements View.OnClickListener {

    Typeface mLight, mBold, mLightItalic;
    TextView[] mFilterText = new TextView[3];
    TextView currentSelection;

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

        MaterialStaggeredGridView mListView = (MaterialStaggeredGridView) view.findViewById(R.id.gridview);
//        mListView.setAdapter(new GridViewAdapterBeacon(getActivity()));

        CustomBeaconCard card = new CustomBeaconCard();

        mListView.add(card);
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
}
