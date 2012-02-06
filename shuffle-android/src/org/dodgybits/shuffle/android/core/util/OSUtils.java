package org.dodgybits.shuffle.android.core.util;

import java.lang.reflect.Field;

import android.os.Build;

public class OSUtils {

    public static boolean osAtLeastFroyo() {
        return osAtLeast(Build.VERSION_CODES.FROYO);
    }

    public static boolean osAtLeastHoneycomb() {
        return osAtLeast(Build.VERSION_CODES.HONEYCOMB);
    }

    public static boolean osAtLeastICS() {
        return osAtLeast(Build.VERSION_CODES.ICE_CREAM_SANDWICH);
    }

    private static boolean osAtLeast(int requiredVersion) {
        boolean isRequiredOrAbove = false;
        try {
            Field field = Build.VERSION.class.getDeclaredField("SDK_INT");
            int version = field.getInt(null);
            isRequiredOrAbove = version >= requiredVersion;
        } catch (Exception e) {
            // ignore exception - field not available
        }
        return isRequiredOrAbove;
    }

    
}
