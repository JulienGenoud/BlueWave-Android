package debas.com.beaconnotifier.preferences;

import java.io.Serializable;
import java.util.List;

/**
 * ** Created by julien on 23/02/15.
 */
public class Prefs {
//    public static final String[] PREFS_FILER_LIST = {
//            "Musées", "Ville", "Magasins", "Historique", "Aéroports", "Entreprises", "Non classés" };
    public static String BEACON_FILTER = "beacon_filter";

    private List<PreferenceFilterBeacon> mPreferenceFilterBeaconList;

    public void setPreferenceFilterBeaconList(List<PreferenceFilterBeacon> mPreferenceFilterBeaconList) {
        this.mPreferenceFilterBeaconList = mPreferenceFilterBeaconList;
    }

    public List<PreferenceFilterBeacon> getPreferenceFilterBeaconList() {
        return mPreferenceFilterBeaconList;
    }

    public static class PreferenceFilterBeacon implements Serializable {
        public String Title;
        public boolean Checked;
    }
}