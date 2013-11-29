package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.list.model.ListQuery;

public class ListSettingsUpdatedEvent {
    private ListQuery mListQuery;

    public ListSettingsUpdatedEvent(ListQuery listQuery) {
        mListQuery = listQuery;
    }

    public ListQuery getListQuery() {
        return mListQuery;
    }
}
