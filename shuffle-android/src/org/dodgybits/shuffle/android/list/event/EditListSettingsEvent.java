package org.dodgybits.shuffle.android.list.event;

import org.dodgybits.shuffle.android.list.model.ListQuery;

public class EditListSettingsEvent {
    private ListQuery mListQuery;
    private int mRequestCode;

    public EditListSettingsEvent(ListQuery query, int requestCode) {
        mListQuery = query;
        mRequestCode = requestCode;
    }

    public ListQuery getListQuery() {
        return mListQuery;
    }

    public int getRequestCode() {
        return mRequestCode;
    }
}
