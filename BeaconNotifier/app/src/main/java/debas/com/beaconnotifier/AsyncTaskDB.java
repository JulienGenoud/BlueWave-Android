package debas.com.beaconnotifier;

/**
 * Created by debas on 21/10/14.
 */
public class AsyncTaskDB {
    public interface OnTaskCompleted <E> {
        public void onTaskCompleted(E jsonObject);
    }
    public interface OnDBUpdated {
        public void onDBUpdated(boolean result, int nbElement);
    }
}
