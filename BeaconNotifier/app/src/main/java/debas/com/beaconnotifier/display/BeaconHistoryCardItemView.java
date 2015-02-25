package debas.com.beaconnotifier.display;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.model.CardItemView;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import debas.com.beaconnotifier.R;

/**
 * Created by debas on 18/02/15.
 */
public class BeaconHistoryCardItemView extends CardItemView<BeaconHistoryCard> {

    private BeaconHistoryCard mBeaconHistoryCard = null;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBeaconHistoryCard != null) {
                mBeaconHistoryCard.getOnHistoryBeaconClickListener().onBeaconClick(v, mBeaconHistoryCard.getBeaconItemSeen());
            }

        }
    };

    public BeaconHistoryCardItemView(Context context) {
        super(context);
    }

    public BeaconHistoryCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BeaconHistoryCardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void build(final BeaconHistoryCard beaconHistoryCard) {
        TextView textView = (TextView) findViewById(R.id.name_beacon);
        final RelativeTimeTextView lastTimeSeen = (RelativeTimeTextView) findViewById(R.id.last_time_seen);

        textView.setText(beaconHistoryCard.getBeaconItemSeen().mNotification);
        lastTimeSeen.setReferenceTime(beaconHistoryCard.getBeaconItemSeen().mSeen);
        textView.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        mBeaconHistoryCard = beaconHistoryCard;

        /* favorites image click */
        ImageView favoritesImageView = (ImageView) findViewById(R.id.favorites_heart);
        favoritesImageView.setImageResource(beaconHistoryCard.getBeaconItemSeen().mFavorites ? R.drawable.favorites_full : R.drawable.favorites_empty);
        favoritesImageView.setOnClickListener(mOnClickListener);

        /* global click card */
        setOnClickListener(mOnClickListener);
    }
}
