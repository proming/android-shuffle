package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class UpdateProjectDeletedEvent {
    private Id mProjectId;
    private boolean mDeleted;

    public UpdateProjectDeletedEvent(Id projectId, boolean deleted) {
        mProjectId = projectId;
        mDeleted = deleted;
    }

    public Id getProjectId() {
        return mProjectId;
    }

    public boolean isDeleted() {
        return mDeleted;
    }
}
