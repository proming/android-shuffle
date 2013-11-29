package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class EditTaskEvent {

    private Id mTaskId;

    public EditTaskEvent(Id taskId) {
        mTaskId = taskId;
    }

    public Id getTaskId() {
        return mTaskId;
    }
}
