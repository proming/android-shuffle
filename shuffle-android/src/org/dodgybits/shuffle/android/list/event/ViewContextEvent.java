package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class ViewContextEvent {
    private Id mContextId;
    private int mPosition;

    public ViewContextEvent(Id contextId) {
        this(contextId, -1);
    }

    public ViewContextEvent(Id contextId, int position) {
        mContextId = contextId;
        mPosition = position;
    }

    public Id getContextId() {
        return mContextId;
    }

    public int getPosition() {
        return mPosition;
    }
}
