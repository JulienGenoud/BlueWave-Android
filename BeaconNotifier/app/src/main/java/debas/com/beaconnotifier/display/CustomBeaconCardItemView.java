package debas.com.beaconnotifier.display;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dexafree.materialList.model.CardItemView;

import java.util.Date;

import debas.com.beaconnotifier.R;

/**
 * Created by debas on 18/02/15.
 */
public class CustomBeaconCardItemView extends CardItemView<CustomBeaconCard> {
    public CustomBeaconCardItemView(Context context) {
        super(context);
        init(context);
    }

    public CustomBeaconCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomBeaconCardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "Long click !", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void build(CustomBeaconCard customBeaconCard) {
        TextView textView = (TextView) findViewById(R.id.name_beacon);
        TextView lastTimeSeen = (TextView) findViewById(R.id.last_time_seen);

        textView.setText(customBeaconCard.getBeaconItemSeen().mNotification);
        lastTimeSeen.setText(new Date(customBeaconCard.getBeaconItemSeen().mSeen).toString());
        textView.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }
}
