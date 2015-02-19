package debas.com.beaconnotifier.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.model.BeaconItemView;

/**
 * Created by debas on 17/02/15.
 */
public class GridViewAdapterBeacon extends BaseAdapter {

    private final Context mContext;
    private List<BeaconItemView> mBeaconItemViewList;

    public GridViewAdapterBeacon(Context context) {
        mContext = context;

        mBeaconItemViewList = new ArrayList<>();
        mBeaconItemViewList.add(new BeaconItemView());
        mBeaconItemViewList.add(new BeaconItemView());
        mBeaconItemViewList.add(new BeaconItemView());
        mBeaconItemViewList.add(new BeaconItemView());
    }

    @Override
    public int getCount() {
        return 15;
    }

    @Override
    public Object getItem(int position) {
        return mBeaconItemViewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.beacon_item_view, parent, false);
        }
//        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view_beacon);
//        if (position % 2 == 0)
//            imageView.setImageResource(R.drawable.example);
//        else
//            imageView.setImageResource(R.drawable.icon_bluewave);
        return convertView;
    }
}
