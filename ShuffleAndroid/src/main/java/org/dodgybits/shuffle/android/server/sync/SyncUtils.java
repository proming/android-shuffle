package org.dodgybits.shuffle.android.server.sync;

import android.content.Context;

import org.dodgybits.shuffle.android.preference.model.Preferences;


public class SyncUtils {

    public static boolean isSyncOn(Context context) {
        return Preferences.isSyncEnabled(context) && Preferences.getSyncAccount(context) != null;
    }
}
