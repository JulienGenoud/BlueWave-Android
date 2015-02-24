package debas.com.beaconnotifier.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import debas.com.beaconnotifier.OnHistoryBeaconClickListener;
import debas.com.beaconnotifier.R;

/**
 * Created by debas on 23/02/15.
 */
public class SearchViewAdapter extends CursorAdapter {
    private OnHistoryBeaconClickListener mOnHistoryBeaconClickListener;

    public SearchViewAdapter(Context context, Cursor c, OnHistoryBeaconClickListener onHistoryBeaconClickListener) {
        super(context, c, false);
        this.mOnHistoryBeaconClickListener = onHistoryBeaconClickListener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_history_search, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        String[] strings = cursor.getColumnNames();
        for (int i = 0; i < strings.length; i++) {
            System.out.println("column " + i + " : " + strings[i]);
        }
        TextView textView = (TextView) view.findViewById(R.id.item);
        textView.setText(cursor.getString(cursor.getColumnIndex("M_NOTIFICATION")));

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BeaconItemSeen beaconItemSeen = new BeaconItemSeen();
//                beaconItemSeen.mSeen = cursor.getLong(cursor.getColumnIndex("M_SEEN"));
//                beaconItemSeen.mFavorites = (cursor.getInt(cursor.getColumnIndex("M_FAVORITES")) == 1);
//                beaconItemSeen.mBeaconId = cursor.getString(cursor.getColumnIndex("M_BEACON_ID"));
//                beaconItemSeen.mUuid = cursor.getString(cursor.getColumnIndex("M_UUID"));
//                beaconItemSeen.mNotification = cursor.getString(cursor.getColumnIndex("M_NOTIFICATION"));
//                beaconItemSeen.mMajor = cursor.getInt(cursor.getColumnIndex("M_MAJOR"));
//                beaconItemSeen.mMinor = cursor.getInt(cursor.getColumnIndex("M_MINOR"));
//                beaconItemSeen.mRange = cursor.getInt(cursor.getColumnIndex("M_RANGE"));
//
//                mOnHistoryBeaconClickListener.onBeaconClick(v, beaconItemSeen);
//            }
//        });
    }


}
