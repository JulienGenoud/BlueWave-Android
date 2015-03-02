package debas.com.beaconnotifier.display.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.preferences.PreferencesHelper;
import debas.com.beaconnotifier.preferences.Prefs;

/**
 * Created by debas on 18/10/14.
 */
public class PreferencesFragment extends BaseFragment {

    private List<Prefs.PreferenceFilterBeacon> mPreferenceFilterBeacons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preferences, container, false);

        TypedArray prefsFilterBeacon = getActivity().getResources().obtainTypedArray(R.array.pref_beacon_filter);
        mPreferenceFilterBeacons = PreferencesHelper.getFilterBeacon(getActivity(), prefsFilterBeacon);

        LinearLayout prefsContainer = (LinearLayout) rootView.findViewById(R.id.layout_list_preferences);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0 ; i < mPreferenceFilterBeacons.size(); i++) {
            LinearLayout beaconFilter = (LinearLayout) layoutInflater.inflate(R.layout.list_item, null, false);

            beaconFilter.setOnClickListener(mOnClickListener);
            ((TextView) beaconFilter.findViewById(R.id.text_preferences)).setText(mPreferenceFilterBeacons.get(i).Title);
            beaconFilter.setTag(mPreferenceFilterBeacons.get(i).Checked);
            setFilterBeacon(beaconFilter, (Boolean) beaconFilter.getTag(), false);
            prefsContainer.addView(beaconFilter);
        }

        return rootView;
    }

    private void setFilterBeacon(LinearLayout beaconFilter, boolean selected, boolean updatePref) {
        ImageView imageView = (ImageView) beaconFilter.findViewById(R.id.image_preferences);
        TextView textView = (TextView) beaconFilter.findViewById(R.id.text_preferences);

        textView.setTextColor(getResources().getColor(selected ? R.color.preferences_filter_text_checked : R.color.preferences_filter_text_unchecked));
        imageView.setImageResource(selected ? R.drawable.ic_hideable_item_checked : R.drawable.ic_hideable_item_unchecked);
        if (updatePref)
            PreferencesHelper.setBeaconFilter(getActivity(), textView.getText().toString(), selected);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean selected = !(boolean) v.getTag();
            setFilterBeacon((LinearLayout) v, selected, true);
            v.setTag(selected);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void buildMenu(Menu menu) {

    }
}
