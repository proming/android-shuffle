package org.dodgybits.shuffle.android.server.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import static org.dodgybits.shuffle.android.server.sync.SyncSchedulingService.ALARM_SOURCE;
import static org.dodgybits.shuffle.android.server.sync.SyncSchedulingService.SOURCE_EXTRA;

public class SyncAlarmReceiver extends BroadcastReceiver {
    public static final String TAG = "SyncAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received intent " + intent.getExtras());
        Intent syncIntent = new Intent(context, SyncSchedulingService.class);
        syncIntent.putExtra(SOURCE_EXTRA, ALARM_SOURCE);
        WakefulBroadcastReceiver.startWakefulService(context, syncIntent);
    }
}
