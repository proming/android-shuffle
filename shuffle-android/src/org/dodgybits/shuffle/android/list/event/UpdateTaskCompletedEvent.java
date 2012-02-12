package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class UpdateTaskCompletedEvent {
    private Id mTaskId;
    private boolean mCompleted;

    public UpdateTaskCompletedEvent(Id taskId, boolean completed) {
        mTaskId = taskId;
        mCompleted = completed;
    }

    public Id getTaskId() {
        return mTaskId;
    }

    public boolean isCompleted() {
        return mCompleted;
    }
}
