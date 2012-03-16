package org.dodgybits.shuffle.android.core.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.activity.ContextTaskListsActivity;
import org.dodgybits.shuffle.android.list.activity.EntityListsActivity;
import org.dodgybits.shuffle.android.list.activity.ProjectTaskListsActivity;
import org.dodgybits.shuffle.android.list.view.task.TaskListContext;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.view.activity.TaskPagerActivity;

public class IntentUtils {
    private static final String TAG = "IntentUtils";
    
    public static Intent createNewTaskIntent(Context context, TaskListContext listContext) {
        TaskSelector selector = listContext.createSelectorWithPreferences(context);
        return createNewTaskIntent(null, selector.getContextId(), selector.getProjectId());
    }

    public static Intent createNewTaskIntent(String description, Id contextId, Id projectId) {
        Intent intent = new Intent(Intent.ACTION_INSERT, TaskProvider.Tasks.CONTENT_URI);
        if (contextId.isInitialised()) {
            intent.putExtra(TaskProvider.Tasks.CONTEXT_ID, contextId.getId());
        }
        if (projectId.isInitialised()) {
            intent.putExtra(TaskProvider.Tasks.PROJECT_ID, projectId.getId());
        }
        if (description != null) {
            intent.putExtra(TaskProvider.Tasks.DESCRIPTION, description);
        }
        return intent;
    }
    
    public static Intent createTaskListIntent(Context context, TaskListContext listContext) {
        Intent intent = new Intent();
        Uri uri = TaskProvider.Tasks.LIST_CONTENT_URI.buildUpon().appendPath(listContext.getListQuery().name()).build();
        intent.setData(uri);
        Class activityClass;
        TaskSelector selector;
        switch (listContext.getListQuery()) {
            case project:
                activityClass = ProjectTaskListsActivity.class;
                selector = listContext.createSelectorWithPreferences(context);
                intent.putExtra(ProjectTaskListsActivity.INITIAL_ID, selector.getProjectId().getId());
                break;
            case context:
                activityClass = ContextTaskListsActivity.class;
                selector = listContext.createSelectorWithPreferences(context);
                intent.putExtra(ContextTaskListsActivity.INITIAL_ID, selector.getContextId().getId());
                break;
            default:
                activityClass = EntityListsActivity.class;
                intent.putExtra(EntityListsActivity.QUERY_NAME, listContext.getListQuery().name());
                break;
        }

        intent.setClass(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
    
    public static Intent createTaskViewIntent(Context context, TaskListContext listContext, int position) {
        Intent intent = new Intent(context, TaskPagerActivity.class);
        intent.putExtra(TaskPagerActivity.INITIAL_POSITION, position);
        intent.putExtra(TaskPagerActivity.TASK_LIST_CONTEXT, listContext);
        return intent;
    }

}
