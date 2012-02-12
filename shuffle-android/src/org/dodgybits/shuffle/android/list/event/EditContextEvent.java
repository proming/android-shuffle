package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.core.model.Id;

public class EditContextEvent {

    private Id mContextId;

    public EditContextEvent(Id contextId) {
        mContextId = contextId;
    }

    public Id getContextId() {
        return mContextId;
    }
}
