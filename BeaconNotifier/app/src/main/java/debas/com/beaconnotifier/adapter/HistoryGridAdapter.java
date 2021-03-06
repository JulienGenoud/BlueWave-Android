package debas.com.beaconnotifier.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.model.BeaconItemSeen;
import debas.com.beaconnotifier.utils.Utils;

/**
 * Created by debas on 28/02/15.
 */
public class HistoryGridAdapter extends CursorAdapter {

    private View.OnClickListener mOnClickListner;

    public HistoryGridAdapter(Context context, View.OnClickListener onClickListener) {
        super(context, null, false);
        this.mOnClickListner = onClickListener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.beacon_item_view, null);
//        v.setLongClickable(true);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final BeaconItemSeen beaconItemSeen = BeaconItemSeen.fromCursor(cursor);

        TextView textView = (TextView) view.findViewById(R.id.name_beacon);
        RelativeTimeTextView lastTimeSeen = (RelativeTimeTextView) view.findViewById(R.id.last_time_seen);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        imageView.setImageResource(Utils.getAssociatedImage(beaconItemSeen.mMajor, beaconItemSeen.mMinor));
        textView.setText(beaconItemSeen.mNotification);
        lastTimeSeen.setReferenceTime(beaconItemSeen.mSeen);
        textView.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        /* favorites image click */
        ImageView favoritesImageView = (ImageView) view.findViewById(R.id.favorites_heart);
        favoritesImageView.setImageResource(beaconItemSeen.mFavorites ? R.drawable.favorites_full : R.drawable.favorites_empty);
        favoritesImageView.setOnClickListener(mOnClickListner);
        favoritesImageView.setTag(beaconItemSeen);
        view.setTag(beaconItemSeen);
    }
}
