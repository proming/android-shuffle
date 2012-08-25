package org.dodgybits.shuffle.android.editor.activity;

import com.google.inject.Inject;
import org.dodgybits.shuffle.android.editor.fragment.AbstractEditFragment;
import org.dodgybits.shuffle.android.editor.fragment.EditProjectFragment;

public class EditProjectActivity extends AbstractEditActivity {
    private static final String TAG = "EditProjectActivity";

    @Inject
    private EditProjectFragment mEditFragment;

    @Override
    protected AbstractEditFragment getFragment() {
        return mEditFragment;
    }

    @Override
    protected void setFragment(AbstractEditFragment fragment) {
        mEditFragment = (EditProjectFragment) fragment;
    }
}
