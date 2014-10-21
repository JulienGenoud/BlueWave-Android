package debas.com.beaconnotifier.database;

import org.json.JSONObject;

/**
 * Created by debas on 21/10/14.
 */
public class AsyncTaskDB {
    public interface OnTaskCompleted {
        void onTaskCompleted(JSONObject jsonObject);
    }
    public interface OnDBUpdated {
        void onDBUpdated(boolean result, int nbElement);
    }
}
