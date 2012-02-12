package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class NewTaskEvent {
    
    private Id mContextId;
    private Id mProjectId;

    public NewTaskEvent(Id contextId, Id projectId) {
        mContextId = contextId;
        mProjectId = projectId;
    }

    public Id getContextId() {
        return mContextId;
    }

    public Id getProjectId() {
        return mProjectId;
    }
}
