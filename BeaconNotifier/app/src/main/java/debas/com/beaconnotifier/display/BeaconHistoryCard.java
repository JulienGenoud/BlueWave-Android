package debas.com.beaconnotifier.display;

import com.dexafree.materialList.model.Card;

import debas.com.beaconnotifier.OnHistoryBeaconClickListener;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 18/02/15.
 */
public class BeaconHistoryCard extends Card {

    private BeaconItemSeen mBeaconItemSeen;
    private OnHistoryBeaconClickListener mOnClickListener;

    public OnHistoryBeaconClickListener getOnHistoryBeaconClickListener() {
        return mOnClickListener;
    }

    @Override
    public int getLayout() {
        return R.layout.beacon_item_view;
    }

    public BeaconItemSeen getBeaconItemSeen() {
        return mBeaconItemSeen;
    }

    public BeaconHistoryCard(BeaconItemSeen beaconItemSeen, OnHistoryBeaconClickListener onClickListener) {
        this.mBeaconItemSeen = beaconItemSeen;
        this.mOnClickListener = onClickListener;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BeaconHistoryCard) {
            return (((BeaconHistoryCard) o).getBeaconItemSeen().equals(mBeaconItemSeen));
        }
        return false;
    }
}
