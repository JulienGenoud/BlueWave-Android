package debas.com.beaconnotifier.display.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.preferences.Prefs;

/**
 * Created by debas on 18/10/14.
 */
public class PreferencesFragment extends BaseFragment {

    public static final String ARG_SCROLL_Y = "ARG_SCROLL_Y";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preferences, container, false);

        ListView lv = (ListView) rootView.findViewById(R.id.list);
        lv.setAdapter(new MyAdapter());

        final ObservableScrollView scrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll);
        Activity parentActivity = getActivity();
        scrollView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));
        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            scrollView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Prefs.PREFS.length;
        }

        @Override
        public String getItem(int position) {
            return Prefs.PREFS[position];
        }

        @Override
        public long getItemId(int position) {
            return Prefs.PREFS[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item, container, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position));



            return convertView;
        }
    }
}
