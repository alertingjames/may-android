package bookclub.technion.maymsgphoto.classes;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.view.Display;
import android.view.Surface;

/**
 * Created by a on 5/18/2017.
 */

public class ScreenLocker {
    final private static String ROTATION_LOCKED_KEY = "LockedOrientationVal";
    final private static String ROTATION_IS_LOCKED_KEY = "IsRotationLocked";
    final private static String ROTATION_SAVED_KEY = "SavedOrientationVal";

    public static int getScreenOrientation(Activity activity) {
        final Display display = activity.getWindowManager().getDefaultDisplay();
        final int rotation = display.getRotation();

        Point size = new Point();
        display.getSize(size);

        final boolean isWiderThanTall = size.x > size.y;

        final boolean isRotatedOrthogonally = (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270);
        int orientation;

        if (isRotatedOrthogonally) {
            if (isWiderThanTall)
                orientation = (rotation ==  Surface.ROTATION_90) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            else
                orientation = (rotation == Surface.ROTATION_90) ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; // normal and reversed switched intended
        }
        else {
            if (isWiderThanTall)
                orientation = (rotation ==  Surface.ROTATION_0) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            else
                orientation = (rotation == Surface.ROTATION_0) ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        }

        return orientation;
    }

    public static void toggleScreenOrientationLock(Activity activity, SharedPreferences prefs, boolean lock) {
        if(lock)
            lockScreenOrientation(activity, prefs);
        else
            unlockScreenOrientation(activity, prefs);
    }


    // call this from your activity's onCreate() or onResume()
    public static boolean restoreScreenLock(Activity activity, SharedPreferences prefs) {
        final boolean isLocked = prefs.getBoolean(ROTATION_IS_LOCKED_KEY, false);
        final int previousLockedOrientation = prefs.getInt(ROTATION_LOCKED_KEY, -999);

        if(isLocked && previousLockedOrientation != -999) {
            prefs.edit().putInt(ROTATION_SAVED_KEY, activity.getRequestedOrientation()).apply();
            activity.setRequestedOrientation(previousLockedOrientation);
            return true;
        }
        return false;
    }

    private static void lockScreenOrientation(Activity activity, SharedPreferences prefs) {
        final int currentOrientation = activity.getRequestedOrientation();
        final int lockOrientation = getScreenOrientation(activity);

        // checking isCurrentlyLocked prevents the ROTATION_LOCKED_KEY and ROTATION_SAVED_KEY
        // becoming identical, which results in the screen not being able to be unlocked.
        final boolean isCurrentlyLocked = prefs.getBoolean(ROTATION_IS_LOCKED_KEY, false);

        if(!isCurrentlyLocked) {
            activity.setRequestedOrientation(lockOrientation);
            prefs.edit()
                    .putInt(ROTATION_SAVED_KEY, currentOrientation)
                    .putInt(ROTATION_LOCKED_KEY, lockOrientation)
                    .putBoolean(ROTATION_IS_LOCKED_KEY, true)
                    .apply();
        }
    }

    private static void unlockScreenOrientation(Activity activity, SharedPreferences prefs) {
        final int savedOrientation = prefs.getInt(ROTATION_SAVED_KEY, activity.getRequestedOrientation());
        activity.setRequestedOrientation(savedOrientation);
        prefs.edit().putBoolean(ROTATION_IS_LOCKED_KEY, false).apply();
    }
}
