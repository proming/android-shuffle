package org.dodgybits.shuffle.android.list.view.task;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Parcel;
import android.os.Parcelable;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.event.EditNewTaskEvent;
import org.dodgybits.shuffle.android.list.event.NewTaskEvent;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.model.ListSettingsCache;
import org.dodgybits.shuffle.android.list.model.ListTitles;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.preference.model.ListSettings;

public class TaskListContext implements Parcelable {
    public static final String LIST_NAME = "listName";
    public static final String CONTEXT_IDS = "contextIds";
    public static final String PROJECT_IDS = "projectIds";

    protected TaskSelector mSelector;
    protected int mTitleId;

    public static final Parcelable.Creator<TaskListContext> CREATOR
            = new Parcelable.Creator<TaskListContext>() {

        @Override
        public TaskListContext createFromParcel(Parcel source) {
            TaskSelector selector = source.readParcelable(TaskSelector.class.getClassLoader());
            return create(selector);
        }

        @Override
        public TaskListContext[] newArray(int size) {
            return new TaskListContext[size];
        }
    };

    public static final TaskListContext createForContext(Id contextId) {
        TaskSelector selector = TaskSelector.newBuilder().setListQuery(ListQuery.context).
                setContextId(contextId).build();
        return create(selector);
    }

    public static final TaskListContext createForProject(Id projectId) {
        TaskSelector selector = TaskSelector.newBuilder().setListQuery(ListQuery.project).
                setProjectId(projectId).
                setSortOrder(TaskProvider.Tasks.DISPLAY_ORDER + " ASC").
                build();
        return create(selector);
    }

    public static final TaskListContext create(ListQuery query) {
        TaskSelector selector = TaskSelector.newBuilder().setListQuery(query).build();
        return create(selector);
    }

    private static final TaskListContext create(TaskSelector selector) {
        ListQuery query = selector.getListQuery();
        return new TaskListContext(selector, ListTitles.getTitleId(query));
    }

    protected TaskListContext(TaskSelector selector, int titleId) {
        mSelector = selector;
        mTitleId = titleId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mSelector, 0);
    }

    public ListQuery getListQuery() {
        return mSelector.getListQuery();
    }

    public TaskSelector createSelectorWithPreferences(Context context) {
        ListSettings settings = ListSettingsCache.findSettings(mSelector.getListQuery());
        return mSelector.builderFrom().applyListPreferences(context, settings).build();
    }
    
    public String createTitle(ContextWrapper context, 
                              EntityCache<org.dodgybits.shuffle.android.core.model.Context> contextCache, 
                              EntityCache<Project> projectCache) {
        String title;
        if (mSelector.getContextId().isInitialised()) {
            title = context.getString(mTitleId, contextCache.findById(mSelector.getContextId()).getName());
        } else if (mSelector.getProjectId().isInitialised()) {
            title = context.getString(mTitleId, projectCache.findById(mSelector.getProjectId()).getName());
        } else {
            title = context.getString(mTitleId);
        }
        
        return title;
    }

    public EditNewTaskEvent createEditNewTaskEvent() {
        return new EditNewTaskEvent(mSelector.getContextId(), mSelector.getProjectId());
    }

    public NewTaskEvent createNewTaskEventWithDescription(String description) {
        return new NewTaskEvent(description, mSelector.getContextId(), mSelector.getProjectId());
    }

    public boolean isQuickAddEnabled(Context context) {
        ListSettings settings = ListSettingsCache.findSettings(mSelector.getListQuery());
        return settings.getQuickAdd(context);
    }
    
    @Override
    public String toString() {
        return "[TaskListContext " + mSelector.getListQuery() + "]";
    }

}
