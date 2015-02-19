package debas.com.beaconnotifier.display;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dexafree.materialList.model.CardItemView;

import debas.com.beaconnotifier.R;

/**
 * Created by debas on 18/02/15.
 */
public class CustomBeaconCardItemView extends CardItemView<CustomBeaconCard> {
    public CustomBeaconCardItemView(Context context) {
        super(context);
    }

    public CustomBeaconCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBeaconCardItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void build(CustomBeaconCard customBeaconCard) {
        TextView textView = (TextView) findViewById(R.id.name_beacon);
        textView.setPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }
}
