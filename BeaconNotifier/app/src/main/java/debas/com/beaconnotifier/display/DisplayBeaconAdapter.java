package debas.com.beaconnotifier.display;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 01/10/14.
 */
public class DisplayBeaconAdapter extends BaseAdapter {

        private List<BeaconItemSeen> mBeaconList = null;
        private Context mContext = null;
        private LayoutInflater mLayoutInflater = null;
        private int updated = 0;

        public DisplayBeaconAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
            mBeaconList = new ArrayList<>();
            mBeaconList.clear();
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mBeaconList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return mBeaconList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        public void setBeaconList(List<BeaconItemSeen> beacons) {
            mBeaconList = beacons;
            updated++;
        }

    public List<BeaconItemSeen> getBeaconList() {
        return mBeaconList;
    }

    @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {

            if(arg1 == null) {
                arg1 = mLayoutInflater.inflate(R.layout.listitem, arg2, false);
            }

            TextView beacon_UUID = (TextView)arg1.findViewById(R.id.Beacon_UUID);
            TextView beacon_Distance = (TextView)arg1.findViewById(R.id.Beacon_Distance);

            if (mBeaconList.size() > arg0) {
                BeaconItemSeen beacon = mBeaconList.get(arg0);
                beacon_UUID.setText(beacon.mUuid + " - " + beacon.mMajor + " - " + beacon.mMinor);
                beacon_Distance.setText(String.format("%.2f meters away", beacon.mDistance) + " - updated : " + updated);
            }
            return arg1;
        }
}
