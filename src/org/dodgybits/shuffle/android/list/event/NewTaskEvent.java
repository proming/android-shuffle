package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class NewTaskEvent {

    private Id mContextId = Id.NONE;
    private Id mProjectId = Id.NONE;
    private String mDescription;

    public NewTaskEvent(String description, Id contextId, Id projectId) {
        mContextId = contextId;
        mProjectId = projectId;
        mDescription = description;
    }

    public Id getContextId() {
        return mContextId;
    }

    public Id getProjectId() {
        return mProjectId;
    }

    public String getDescription() {
        return mDescription;
    }
}
