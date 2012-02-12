package org.dodgybits.shuffle.android.list.listener;

import android.app.Activity;
import android.widget.Toast;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.list.event.UpdateContextDeletedEvent;
import org.dodgybits.shuffle.android.list.event.UpdateProjectDeletedEvent;
import org.dodgybits.shuffle.android.list.event.UpdateTaskCompletedEvent;
import org.dodgybits.shuffle.android.list.event.UpdateTaskDeletedEvent;
import roboguice.event.Observes;

public class EntityUpdateListener {

    private Activity mActivity;
    private ProjectPersister mProjectPersister;
    private ContextPersister mContextPersister;
    private TaskPersister mTaskPersister;
    
    @Inject
    public EntityUpdateListener(Activity activity, ProjectPersister projectPersister,
                                ContextPersister contextPersister, TaskPersister taskPersister) {
        mActivity = activity;
        mProjectPersister = projectPersister;
        mContextPersister = contextPersister;
        mTaskPersister = taskPersister;
    }

    public void onToggleProjectDeleted(@Observes UpdateProjectDeletedEvent event) {
        mProjectPersister.updateDeletedFlag(event.getProjectId(), event.isDeleted());
        String entityName = mActivity.getString(R.string.project_name);
        if (event.isDeleted()) {
            showDeletedToast(entityName);
        } else {
            showSavedToast(entityName);
        }
    }

    public void onToggleContextDeleted(@Observes UpdateContextDeletedEvent event) {
        mContextPersister.updateDeletedFlag(event.getContextId(), event.isDeleted());
        String entityName = mActivity.getString(R.string.context_name);
        if (event.isDeleted()) {
            showDeletedToast(entityName);
        } else {
            showSavedToast(entityName);
        }
    }

    public void onToggleTaskDeleted(@Observes UpdateTaskDeletedEvent event) {
        mTaskPersister.updateDeletedFlag(event.getTaskId(), event.isDeleted());
        String entityName = mActivity.getString(R.string.task_name);
        if (event.isDeleted()) {
            showDeletedToast(entityName);
        } else {
            showSavedToast(entityName);
        }
    }

    public void onToggleTaskCompleted(@Observes UpdateTaskCompletedEvent event) {
        mTaskPersister.updateCompleteFlag(event.getTaskId(), event.isCompleted());
        String entityName = mActivity.getString(R.string.task_name);
        showSavedToast(entityName);
    }

    private void showDeletedToast(String entityName) {
        String text = mActivity.getResources().getString(
                R.string.itemDeletedToast, entityName);
        Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show();
    }
    
    private void showSavedToast(String entityName) {
        String text = mActivity.getString(R.string.itemSavedToast, entityName);
        Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show();
    }
    
}
