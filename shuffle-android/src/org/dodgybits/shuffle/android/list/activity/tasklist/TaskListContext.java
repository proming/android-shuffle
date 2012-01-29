package org.dodgybits.shuffle.android.list.activity.tasklist;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.persistence.selector.Flag;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.activity.ListPreferenceActivity;
import org.dodgybits.shuffle.android.list.config.StandardTaskQueries;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

import java.util.HashMap;

public class TaskListContext implements Parcelable {
    public static final String LIST_NAME = "listName";
    public static final String CONTEXT_IDS = "contextIds";
    public static final String PROJECT_IDS = "projectIds";

    private static final String DUE_TASKS_SETTINGS_KEY = "due_tasks";

    private static ListPreferenceSettings dueTaskSettings =
        new ListPreferenceSettings(DUE_TASKS_SETTINGS_KEY).setDefaultCompleted(Flag.no);
    private static ListPreferenceSettings ticklerSettings =
        new ListPreferenceSettings(StandardTaskQueries.cTickler)
            .setDefaultCompleted(Flag.no)
            .setDefaultActive(Flag.no);
    private static ListPreferenceSettings nextTasksSettings =
        new ListPreferenceSettings(StandardTaskQueries.cNextTasks)
            .setDefaultCompleted(Flag.no)
            .disableCompleted()
            .disableDeleted()
            .disableActive();


    private static final HashMap<TaskSelector.PredefinedQuery,ListPreferenceSettings> SPARSE_SETTINGS_MAP =
            new HashMap<TaskSelector.PredefinedQuery,ListPreferenceSettings>();

    static {
        SPARSE_SETTINGS_MAP.put(TaskSelector.PredefinedQuery.nextTasks, nextTasksSettings);
        SPARSE_SETTINGS_MAP.put(TaskSelector.PredefinedQuery.tickler, ticklerSettings);
        SPARSE_SETTINGS_MAP.put(TaskSelector.PredefinedQuery.dueToday, dueTaskSettings);
        SPARSE_SETTINGS_MAP.put(TaskSelector.PredefinedQuery.dueNextWeek, dueTaskSettings);
        SPARSE_SETTINGS_MAP.put(TaskSelector.PredefinedQuery.dueNextMonth, dueTaskSettings);
    }

    private static final HashMap<TaskSelector.PredefinedQuery,Integer> TITLE_ID_MAP =
            new HashMap<TaskSelector.PredefinedQuery,Integer>();

    static {
        TITLE_ID_MAP.put(TaskSelector.PredefinedQuery.all, R.string.title_all);
        TITLE_ID_MAP.put(TaskSelector.PredefinedQuery.context, R.string.title_context_tasks);
        TITLE_ID_MAP.put(TaskSelector.PredefinedQuery.project, R.string.title_project_tasks);
        TITLE_ID_MAP.put(TaskSelector.PredefinedQuery.inbox, R.string.title_inbox);
        TITLE_ID_MAP.put(TaskSelector.PredefinedQuery.nextTasks, R.string.title_next_tasks);
        TITLE_ID_MAP.put(TaskSelector.PredefinedQuery.tickler, R.string.title_tickler);
        TITLE_ID_MAP.put(TaskSelector.PredefinedQuery.dueToday, R.string.title_due_today);
        TITLE_ID_MAP.put(TaskSelector.PredefinedQuery.dueNextWeek, R.string.title_due_next_week);
        TITLE_ID_MAP.put(TaskSelector.PredefinedQuery.dueNextMonth, R.string.title_due_next_month);
    }
    
    private final TaskSelector mSelector;
    private final ListPreferenceSettings mSettings;
    private final int mTitleId;

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

    public static final TaskListContext create(TaskSelector.PredefinedQuery query) {
        TaskSelector selector = TaskSelector.newBuilder().setPredefined(query).build();
        return create(selector);
    }

    private static final TaskListContext create(TaskSelector selector) {
        TaskSelector.PredefinedQuery query = selector.getPredefinedQuery();
        ListPreferenceSettings settings = SPARSE_SETTINGS_MAP.get(query);
        if (settings == null) {
            // if setting is not in the map, it means the query has all the standard default settings
            // just create a new one with the right name
            settings = new ListPreferenceSettings(query.name());
        }
        return new TaskListContext(selector, settings, TITLE_ID_MAP.get(query));
    }
    
    private TaskListContext(TaskSelector selector, ListPreferenceSettings settings, int titleId) {
        mSelector = selector;
        mSettings = settings;
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

    public TaskSelector createSelectorWithPreferences(Context context) {
        return mSelector.builderFrom().applyListPreferences(context, mSettings).build();
    }
    
    public String createTitle(ContextWrapper context) {
        return context.getString(mTitleId);
    }
    
    public String createTitle(ContextWrapper context, String name) {
        return context.getString(mTitleId, name);
    }

    @Override
    public String toString() {
        return "[TaskListContext " + mSelector.getPredefinedQuery() + "]";
    }

    public Intent createListSettingsIntent(Context context) {
        Intent intent = new Intent(context, ListPreferenceActivity.class);
        mSettings.addToIntent(intent);
        return intent;
    }
}
