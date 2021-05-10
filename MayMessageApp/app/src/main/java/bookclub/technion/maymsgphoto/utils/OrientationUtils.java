package bookclub.technion.maymsgphoto.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;

/**
 * Created by a on 5/18/2017.
 */

public class OrientationUtils {
    public OrientationUtils() {}

    /** Locks the device window in landscape mode. */
    public static void lockOrientationLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /** Locks the device window in portrait mode. */
    public static void lockOrientationPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /** Allows user to freely use portrait or landscape mode. */
    public static void unlockOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

}
