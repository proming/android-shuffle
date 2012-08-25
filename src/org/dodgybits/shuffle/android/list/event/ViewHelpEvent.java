package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.list.model.ListQuery;

public class ViewHelpEvent {

    private ListQuery mListQuery;

    public ViewHelpEvent() {
    }

    public ViewHelpEvent(ListQuery query) {
        mListQuery = query;
    }

    public ListQuery getListQuery() {
        return mListQuery;
    }
}
