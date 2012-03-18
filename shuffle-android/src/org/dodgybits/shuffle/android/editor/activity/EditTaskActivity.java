package org.dodgybits.shuffle.android.editor.activity;

import com.google.inject.Inject;
import org.dodgybits.shuffle.android.editor.fragment.AbstractEditFragment;
import org.dodgybits.shuffle.android.editor.fragment.EditTaskFragment;

public class EditTaskActivity extends AbstractEditActivity {
    private static final String TAG = "EditTaskActivity";

    @Inject
    private EditTaskFragment mEditFragment;

    @Override
    protected AbstractEditFragment getFragment() {
        return mEditFragment;
    }

    @Override
    protected void setFragment(AbstractEditFragment fragment) {
        mEditFragment = (EditTaskFragment) fragment;
    }
}
