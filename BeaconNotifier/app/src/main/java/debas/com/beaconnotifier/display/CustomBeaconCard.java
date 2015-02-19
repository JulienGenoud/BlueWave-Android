package debas.com.beaconnotifier.display;

import com.dexafree.materialList.model.Card;

import debas.com.beaconnotifier.R;

/**
 * Created by debas on 18/02/15.
 */
public class CustomBeaconCard extends Card {
    @Override
    public int getLayout() {
        return R.layout.beacon_item_view;
    }

    public CustomBeaconCard() {

    }
}
