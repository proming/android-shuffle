package org.dodgybits.shuffle.android.list.listener;

import android.app.Activity;
import android.widget.Toast;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.list.event.*;
import roboguice.event.Observes;

import java.util.Set;

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

    public void onMoveTasks(@Observes MoveTasksEvent event) {
        // TODO
    }

    public void onToggleTasksDeleted(@Observes UpdateTasksDeletedEvent event) {
        Set<Long> taskIds = event.getTaskIds();
        for (Long taskId : taskIds) {
            Id id = Id.create(taskId);
            mTaskPersister.updateDeletedFlag(id, event.isDeleted());
        }

        String entityName = mActivity.getString(R.string.task_name);
        if (event.isDeleted()) {
            showDeletedToast(entityName);
        } else {
            showSavedToast(entityName);
        }
    }

    public void onToggleTaskCompleted(@Observes UpdateTasksCompletedEvent event) {
        Set<Long> taskIds = event.getTaskIds();
        for (Long taskId : taskIds) {
            Id id = Id.create(taskId);
            mTaskPersister.updateCompleteFlag(id, event.isCompleted());
        }

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
