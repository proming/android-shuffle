package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class UpdateTaskDeletedEvent {
    private Id mTaskId;
    private boolean mDeleted;

    public UpdateTaskDeletedEvent(Id taskId, boolean deleted) {
        mTaskId = taskId;
        mDeleted = deleted;
    }

    public Id getTaskId() {
        return mTaskId;
    }

    public boolean isDeleted() {
        return mDeleted;
    }
}
