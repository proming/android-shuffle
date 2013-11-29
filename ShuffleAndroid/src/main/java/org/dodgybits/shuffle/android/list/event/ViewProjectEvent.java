package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class ViewProjectEvent {
    private Id mProjectId;
    private int mPosition;

    public ViewProjectEvent(Id projectId) {
        this(projectId, -1);
    }

    public ViewProjectEvent(Id projectId, int position) {
        mProjectId = projectId;
        mPosition = position;
    }

    public Id getProjectId() {
        return mProjectId;
    }

    public int getPosition() {
        return mPosition;
    }
}
