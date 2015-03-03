package debas.com.beaconnotifier.display.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.util.List;

import debas.com.beaconnotifier.AsyncTaskDB;
import debas.com.beaconnotifier.BroadCastBluetoothReceiver;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.database.BeaconDataBase;
import debas.com.beaconnotifier.preferences.PreferencesHelper;
import debas.com.beaconnotifier.preferences.Prefs;
import debas.com.beaconnotifier.utils.Utils;

/**
 * Created by debas on 18/10/14.
 */
public class PreferencesFragment extends BaseFragment {

    private List<Prefs.PreferenceFilterBeacon> mPreferenceFilterBeacons;
    private RelativeTimeTextView mRelativeTimeUpdateDB;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private CircularProgressButton buttonRequestUpdate;
    private SwitchCompat switchCompatNotif, switchCompatBluetooth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preferences, container, false);

        switchCompatNotif = (SwitchCompat) rootView.findViewById(R.id.notification_switch);
        if (PreferencesHelper.getNoticationEnable(getActivity())) {
            switchCompatNotif.setChecked(true);
        }
        switchCompatBluetooth = (SwitchCompat) rootView.findViewById(R.id.bluetooth_switch);
        if (Utils.getBluetoothState()) {
            switchCompatBluetooth.setChecked(true);
        }
        switchCompatNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesHelper.setNoticationEnable(getActivity(), ((SwitchCompat) v).isChecked());
            }
        });

        switchCompatBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.changeBluetoothState(((SwitchCompat) v).isChecked());
            }
        });

        BroadCastBluetoothReceiver mReceiver = Utils.getBroadCastBluetoothReceiver();
        mReceiver.addBroadCastBluetoothListner(new BroadCastBluetoothReceiver.BroadCastBluetoothListener() {
            @Override
            public void onChange(int state) {
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        switchCompatBluetooth.setChecked(false);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        switchCompatBluetooth.setChecked(true);
                        break;
                }
            }
        });

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

        mRelativeTimeUpdateDB = (RelativeTimeTextView) rootView.findViewById(R.id.relative_time_update);

        long lastUpdateTime = PreferencesHelper.getLastUpdateDB(getActivity());
        if (lastUpdateTime == 0) {
            mRelativeTimeUpdateDB.setText(R.string.none);
        } else {
            mRelativeTimeUpdateDB.setReferenceTime(lastUpdateTime);
        }

        buttonRequestUpdate = (CircularProgressButton) rootView.findViewById(R.id.circular_progress_update);
        buttonRequestUpdate.setBackgroundColor(getResources().getColor(R.color.primary));
        buttonRequestUpdate.setIndeterminateProgressMode(true);
        buttonRequestUpdate.setStrokeColor(getResources().getColor(R.color.primary));
        buttonRequestUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonRequestUpdate.getProgress() == 0) {
                    buttonRequestUpdate.setProgress(50);
                } else if (buttonRequestUpdate.getProgress() == -1) {
                    buttonRequestUpdate.setIdleText(getString(R.string.update_db));
                    buttonRequestUpdate.setProgress(0);
                    return;
                }
                BeaconDataBase.getInstance(getActivity()).updateDB(getActivity(), new AsyncTaskDB.OnDBUpdated() {
                    @Override
                    public void onDBUpdated(boolean result, int nbElement) {
                        if (result) {
                            buttonRequestUpdate.setIdleText(getString(R.string.complete_update_db));
                            buttonRequestUpdate.setProgress(0);
                        } else {
                            buttonRequestUpdate.setProgress(-1);
                        }
                    }
                });
            }
        });

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equalsIgnoreCase(PreferencesHelper.LAST_TIME_UPDATE_DB)) {
                    mRelativeTimeUpdateDB.setReferenceTime(PreferencesHelper.getLastUpdateDB(getActivity()));
                }
                if (key.equalsIgnoreCase(PreferencesHelper.NOTIFICATION_KEY)) {
                    switchCompatNotif.setChecked(PreferencesHelper.getNoticationEnable(getActivity()));
                }
            }
        };

        getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        .registerOnSharedPreferenceChangeListener(preferenceChangeListener);
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
