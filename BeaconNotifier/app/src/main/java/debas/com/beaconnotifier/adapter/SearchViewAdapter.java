package debas.com.beaconnotifier.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import debas.com.beaconnotifier.R;

/**
 * Created by debas on 23/02/15.
 */
public class SearchViewAdapter extends CursorAdapter {

    public SearchViewAdapter(Context context) {
        super(context, null, false);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_history_search, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            String[] strings = cursor.getColumnNames();
            for (int i = 0; i < strings.length; i++) {
                System.out.println("column " + i + " : " + strings[i]);
            }
            TextView textView = (TextView) view.findViewById(R.id.item);
            textView.setText(cursor.getString(cursor.getColumnIndex("M_NOTIFICATION")));
        }
    }


}
