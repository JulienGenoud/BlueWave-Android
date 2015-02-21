package debas.com.beaconnotifier;

import android.view.View;

import debas.com.beaconnotifier.display.BeaconHistoryCard;

/**
 * Created by debas on 21/02/15.
 */
public abstract class OnHistoryBeaconClickListener {
    public abstract void onBeaconClick(View v, BeaconHistoryCard beaconHistoryCard);
}
