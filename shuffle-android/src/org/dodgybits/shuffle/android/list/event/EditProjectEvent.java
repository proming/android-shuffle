package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class EditProjectEvent {

    private Id mProjectId;

    public EditProjectEvent(Id projectId) {
        this.mProjectId = projectId;
    }

    public Id getProjectId() {
        return mProjectId;
    }
}
