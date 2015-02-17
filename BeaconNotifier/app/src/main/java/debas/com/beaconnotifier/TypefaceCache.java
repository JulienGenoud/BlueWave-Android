package debas.com.beaconnotifier;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by debas on 17/02/15.
 */
public class TypefaceCache {

    private static final Hashtable<String, Typeface> CACHE = new Hashtable<String, Typeface>();

    private static final String COND_BOLD = "fonts/OpenSans-CondBold.ttf";
    private static final String COND_LIGHT = "fonts/OpenSans-CondLight.ttf";
    private static final String COND_LIGHT_ITALIC = "fonts/OpenSans-CondLightItalic.ttf";

    public static Typeface get(AssetManager manager, int typefaceCode) {
        synchronized (CACHE) {

            String typefaceName = getTypefaceName(typefaceCode);

            if (!CACHE.containsKey(typefaceName)) {
                Typeface t = Typeface.createFromAsset(manager, typefaceName);
                CACHE.put(typefaceName, t);
            }
            return CACHE.get(typefaceName);
        }
    }

    private static String getTypefaceName(int typefaceCode) {
        switch (typefaceCode) {
            case 0:
                return COND_LIGHT;

            case 1:
                return COND_LIGHT_ITALIC;

            case 2:
                return COND_BOLD;

            default:
                return null;
        }
    }
}