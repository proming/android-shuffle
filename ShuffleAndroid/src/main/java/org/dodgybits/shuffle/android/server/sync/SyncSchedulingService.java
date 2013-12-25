/*
 * Copyright (C) 2013 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.shuffle.android.server.sync;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Gets notified when events happen that would require a sync and triggers
 * a sync when it deems it necessary.
 */
public class SyncSchedulingService extends IntentService {

    public SyncSchedulingService() {
        super("SyncSchedulingService");
    }
    public static final String TAG = "SyncSchedulingService";

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received intent " + intent.getExtras());

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent syncIntent = new Intent(this, SyncReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, syncIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);

        // Release the wake lock provided by the WakefulBroadcastReceiver
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

}
