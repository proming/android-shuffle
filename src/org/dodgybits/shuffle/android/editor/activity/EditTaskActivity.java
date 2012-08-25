package org.dodgybits.shuffle.android.editor.activity;

import android.app.Dialog;
import android.os.Bundle;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.view.EntityPickerDialogHelper;
import org.dodgybits.shuffle.android.editor.fragment.AbstractEditFragment;
import org.dodgybits.shuffle.android.editor.fragment.EditTaskFragment;

import java.util.List;

public class EditTaskActivity extends AbstractEditActivity {
    private static final String TAG = "EditTaskActivity";

    public static final int CONTEXT_PICKER_DIALOG = 1;

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

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        EntityPickerDialogHelper.OnEntitiesSelected listener;

        switch(id) {
            case CONTEXT_PICKER_DIALOG:
                listener = new EntityPickerDialogHelper.OnEntitiesSelected() {
                    @Override
                    public void onSelected(List<Id> ids) {
                        mEditFragment.setSelectedContextIds(ids);
                        // throw it away each time as need to reset checked items
                        removeDialog(CONTEXT_PICKER_DIALOG);
                    }

                    @Override
                    public void onCancel() {
                        // throw it away each time as need to reset checked items
                        // should probably use DialogFragment instead
                        removeDialog(CONTEXT_PICKER_DIALOG);
                    }
                };
                dialog = EntityPickerDialogHelper.createMultiSelectContextPickerDialog(
                        this, mEditFragment.getSelectedContextIds(), listener);
                break;

            default:
                dialog = super.onCreateDialog(id);
                break;
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        super.onPrepareDialog(id, dialog, args);




    }
}
