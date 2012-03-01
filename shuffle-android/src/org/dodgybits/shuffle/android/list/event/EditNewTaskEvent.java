package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class EditNewTaskEvent {
    
    private Id mContextId;
    private Id mProjectId;
    private String mDescription;

    public EditNewTaskEvent(Id contextId, Id projectId, String description) {
        this(contextId, projectId);
        mDescription = description;
    }

    public EditNewTaskEvent(Id contextId, Id projectId) {
        mContextId = contextId;
        mProjectId = projectId;
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
