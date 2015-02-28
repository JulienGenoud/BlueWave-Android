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
import com.orm.query.Condition;
import com.orm.query.Select;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 28/02/15.
 */
public class HistoryGridAdapter extends CursorAdapter {

    public HistoryGridAdapter(Context context) {
        super(context, null, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.beacon_item_view, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final BeaconItemSeen beaconItemSeen = BeaconItemSeen.fromCursor(cursor);

        TextView textView = (TextView) view.findViewById(R.id.name_beacon);
        final RelativeTimeTextView lastTimeSeen = (RelativeTimeTextView) view.findViewById(R.id.last_time_seen);

        textView.setText(beaconItemSeen.mNotification);
        lastTimeSeen.setReferenceTime(beaconItemSeen.mSeen);
        textView.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        /* favorites image click */
        ImageView favoritesImageView = (ImageView) view.findViewById(R.id.favorites_heart);
        favoritesImageView.setImageResource(beaconItemSeen.mFavorites ? R.drawable.favorites_full : R.drawable.favorites_empty);
        favoritesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) v;
                Class beaconClass = BeaconItemSeen.class;
                Select select = Select.from(beaconClass)
                        .where(Condition.prop("m_uuid").eq(beaconItemSeen.mUuid),
                                Condition.prop("m_major").eq(beaconItemSeen.mMajor),
                                Condition.prop("m_minor").eq(beaconItemSeen.mMinor));
                if (select.count() > 0) {
                    BeaconItemSeen beaconItemSeenSugar = (BeaconItemSeen) select.first();

                    if (beaconItemSeenSugar.mFavorites) {
                        imageView.setImageResource(R.drawable.favorites_empty);
                    } else {
                        imageView.setImageResource(R.drawable.favorites_full);
                    }
                    beaconItemSeenSugar.mFavorites = !beaconItemSeenSugar.mFavorites;
                    beaconItemSeenSugar.save();
                }
            }
        });
        view.setTag(beaconItemSeen);
    }
}
