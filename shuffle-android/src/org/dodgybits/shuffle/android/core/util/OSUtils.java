package org.dodgybits.shuffle.android.core.util;

import android.os.Build;

import java.lang.reflect.Field;

public class OSUtils {

    public static boolean atLeastFroyo() {
        return osAtLeast(Build.VERSION_CODES.FROYO);
    }

    public static boolean atLeastHoneycomb() {
        return osAtLeast(Build.VERSION_CODES.HONEYCOMB);
    }

    public static boolean atLeastICS() {
        return osAtLeast(Build.VERSION_CODES.ICE_CREAM_SANDWICH);
    }

    private static int sVersion = -1;

    private static boolean osAtLeast(int requiredVersion) {
        if (sVersion == -1) {
            try {
                Field field = Build.VERSION.class.getDeclaredField("SDK_INT");
                sVersion = field.getInt(null);
            } catch (Exception e) {
                // ignore exception - field not available
                sVersion = 0;
            }
        }

        return sVersion >= requiredVersion;
    }

    
}
