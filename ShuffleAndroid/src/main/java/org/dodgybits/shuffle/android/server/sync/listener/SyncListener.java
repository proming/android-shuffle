package org.dodgybits.shuffle.android.server.sync.listener;

import android.content.Context;
import android.util.Log;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.server.sync.event.ResetSyncSettingsEvent;
import roboguice.event.Observes;

public class SyncListener {
    private static final String TAG = "SyncListener";


    @Inject
    private Context mContext;

    @Inject
    private TaskPersister mTaskPersister;

    @Inject
    private ProjectPersister mProjectPersister;

    @Inject
    private ContextPersister mContextPersister;

    public void onClearSync(@Observes ResetSyncSettingsEvent event) {
        Log.d(TAG, "Clearing out all sync data");

        mContextPersister.clearAllGaeIds();
        mProjectPersister.clearAllGaeIds();
        mTaskPersister.clearAllGaeIds();

        Preferences.getEditor(mContext)
                .remove(Preferences.SYNC_LAST_SYNC_ID)
                .remove(Preferences.SYNC_LAST_SYNC_GAE_DATE)
                .remove(Preferences.SYNC_LAST_SYNC_LOCAL_DATE)
                .remove(Preferences.SYNC_LAST_SYNC_FAILURE_DATE)
                .commit();
    }


}
