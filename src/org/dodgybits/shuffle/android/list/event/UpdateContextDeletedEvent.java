package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class UpdateContextDeletedEvent {
    private Id mContextId;
    private Boolean mDeleted = null;

    public UpdateContextDeletedEvent(Id contextId) {
        mContextId = contextId;
    }

    public UpdateContextDeletedEvent(Id contextId, boolean deleted) {
        mContextId = contextId;
        mDeleted = deleted;
    }

    public Id getContextId() {
        return mContextId;
    }

    public Boolean isDeleted() {
        return mDeleted;
    }
}
