package org.dodgybits.shuffle.android.list.event;

import android.support.v4.app.Fragment;
import org.dodgybits.shuffle.android.list.model.ListQuery;

public class EditListSettingsEvent {
    private ListQuery mListQuery;
    private final Fragment mFragment;
    private int mRequestCode;

    public EditListSettingsEvent(ListQuery query, Fragment fragment, int requestCode) {
        mListQuery = query;
        mFragment = fragment;
        mRequestCode = requestCode;
    }

    public ListQuery getListQuery() {
        return mListQuery;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    public Fragment getFragment() {
        return mFragment;
    }
}
