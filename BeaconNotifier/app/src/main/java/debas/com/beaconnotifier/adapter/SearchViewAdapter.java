package debas.com.beaconnotifier.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.model.BeaconItemSeen;
import debas.com.beaconnotifier.utils.Utils;

/**
 * Created by debas on 23/02/15.
 */
public class SearchViewAdapter extends CursorAdapter {

    public SearchViewAdapter(Context context) {
        super(context, null, false);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_history_search, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            BeaconItemSeen beaconItemSeen = BeaconItemSeen.fromCursor(cursor);

            ImageView imageView = (ImageView) view.findViewById(R.id.imageview_search);
            imageView.setImageResource(Utils.getAssociatedImage(beaconItemSeen.mMajor, beaconItemSeen.mMinor));
            TextView textView = (TextView) view.findViewById(R.id.item);
            textView.setText(beaconItemSeen.mNotification);
        }
    }


}
