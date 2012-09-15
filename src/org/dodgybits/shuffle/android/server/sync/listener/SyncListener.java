package org.dodgybits.shuffle.android.server.sync.listener;

import android.app.Activity;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.server.sync.ObtainAuthTokenTask;
import org.dodgybits.shuffle.android.server.sync.event.RegisterSyncAccountEvent;
import roboguice.event.Observes;

public class SyncListener {
    private static final String TAG = "SyncListener";

    private Activity mActivity;

    @Inject
    public SyncListener(Activity activity) {
        mActivity = activity;
    }

    public void onRegisterSyncAccount(@Observes RegisterSyncAccountEvent event) {
        if (event.getAccount() != null) {
            new ObtainAuthTokenTask(mActivity, event.getAccount()).execute();
        }
    }


}
