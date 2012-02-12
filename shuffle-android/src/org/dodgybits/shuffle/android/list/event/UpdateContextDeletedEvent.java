package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class UpdateContextDeletedEvent {
    private Id mContextId;
    private boolean mDeleted;

    public UpdateContextDeletedEvent(Id contextId, boolean deleted) {
        mContextId = contextId;
        mDeleted = deleted;
    }

    public Id getContextId() {
        return mContextId;
    }

    public boolean isDeleted() {
        return mDeleted;
    }
}
