package debas.com.beaconnotifier.display;

import com.dexafree.materialList.model.Card;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 18/02/15.
 */
public class CustomBeaconCard extends Card {

    private BeaconItemSeen mBeaconItemSeen;

    @Override
    public int getLayout() {
        return R.layout.beacon_item_view;
    }

    public BeaconItemSeen getBeaconItemSeen() {
        return mBeaconItemSeen;
    }

    public CustomBeaconCard(BeaconItemSeen beaconItemSeen) {
        this.mBeaconItemSeen = beaconItemSeen;

    }
}
