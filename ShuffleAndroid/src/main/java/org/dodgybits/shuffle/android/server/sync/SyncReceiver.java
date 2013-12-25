package org.dodgybits.shuffle.android.server.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncReceiver extends BroadcastReceiver {
    public static final String TAG = "SyncReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received intent " + intent.getExtras());
        Intent syncIntent = new Intent(context, GaeSyncService.class);
        context.startService(syncIntent);
    }
}
