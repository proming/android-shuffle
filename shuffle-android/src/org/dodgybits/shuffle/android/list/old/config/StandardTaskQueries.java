package org.dodgybits.shuffle.android.list.old.config;

import android.content.Context;
import android.content.Intent;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.old.activity.task.InboxActivity;
import org.dodgybits.shuffle.android.list.old.activity.task.TabbedDueActionsActivity;
import org.dodgybits.shuffle.android.list.old.activity.task.TicklerActivity;
import org.dodgybits.shuffle.android.list.old.activity.task.TopTasksActivity;

import java.util.HashMap;

public class StandardTaskQueries {

    public static final String cInbox = "inbox";
    public static final String cDueToday = "due_today";
    public static final String cDueNextWeek = "due_next_week";
    public static final String cDueNextMonth = "due_next_month";
    public static final String cNextTasks = "next_tasks";
    public static final String cTickler = "tickler";
    public static final String cContext = "context";
    public static final String cProject   = "project";

    public static final String cDueTasksFilterPrefs = "due_tasks";
    public static final String cProjectFilterPrefs = "project";
    public static final String cContextFilterPrefs = "context";

    private static final TaskSelector cInboxQuery = 
        TaskSelector.newBuilder().setListQuery(ListQuery.inbox).build();
        
    private static final TaskSelector cDueTodayQuery = 
        TaskSelector.newBuilder().setListQuery(ListQuery.dueToday).build();

    private static final TaskSelector cDueNextWeekQuery = 
        TaskSelector.newBuilder().setListQuery(ListQuery.dueNextWeek).build();

    private static final TaskSelector cDueNextMonthQuery = 
        TaskSelector.newBuilder().setListQuery(ListQuery.dueNextMonth).build();
    
    private static final TaskSelector cNextTasksQuery = 
        TaskSelector.newBuilder().setListQuery(ListQuery.nextTasks).build();

    private static final TaskSelector cTicklerQuery = 
        TaskSelector.newBuilder().setListQuery(ListQuery.tickler).build();
    

    private static final HashMap<String,TaskSelector> cQueryMap = new HashMap<String,TaskSelector>();
    static {
        cQueryMap.put(cInbox, cInboxQuery);
        cQueryMap.put(cDueToday, cDueTodayQuery);
        cQueryMap.put(cDueNextWeek, cDueNextWeekQuery);
        cQueryMap.put(cDueNextMonth, cDueNextMonthQuery);
        cQueryMap.put(cNextTasks, cNextTasksQuery);
        cQueryMap.put(cTickler, cTicklerQuery);
    }

    private static final HashMap<String,String> cFilterPrefsMap = new HashMap<String,String>();
    static {
        cFilterPrefsMap.put(cInbox, cInbox);
        cFilterPrefsMap.put(cDueToday, cDueTasksFilterPrefs);
        cFilterPrefsMap.put(cDueNextWeek, cDueTasksFilterPrefs);
        cFilterPrefsMap.put(cDueNextMonth, cDueTasksFilterPrefs);
        cFilterPrefsMap.put(cNextTasks, cNextTasks);
        cFilterPrefsMap.put(cTickler, cTickler);
    }

    public static TaskSelector getQuery(String name) {
        return cQueryMap.get(name);
    }

    public static String getFilterPrefsKey(String name) {
        return cFilterPrefsMap.get(name);
    }

    @Deprecated
    public static Intent getActivityIntent(Context context, String name) {
        if (cInbox.equals(name)) {
            return new Intent(context, InboxActivity.class);
        }
        if (cNextTasks.equals(name)) {
            return new Intent(context, TopTasksActivity.class);
        }
        if (cTickler.equals(name)) {
            return new Intent(context, TicklerActivity.class);
        }

        ListQuery query = ListQuery.dueToday;
        if (cDueNextWeek.equals(name)) {
            query = ListQuery.dueNextWeek;
        } else if (cDueNextMonth.equals(name)) {
            query = ListQuery.dueNextMonth;
        }
        Intent intent = new Intent(context, TabbedDueActionsActivity.class);
        intent.putExtra(TabbedDueActionsActivity.DUE_MODE, query.name());
        return intent;
    }
    
}
