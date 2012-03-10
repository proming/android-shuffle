package org.dodgybits.shuffle.android.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.util.IntentUtils;
import org.dodgybits.shuffle.android.list.view.task.TaskListContext;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.preference.model.ListSettings;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import java.util.Arrays;
import java.util.HashMap;

import static org.dodgybits.shuffle.android.core.util.Constants.cIdType;
import static org.dodgybits.shuffle.android.core.util.Constants.cPackage;

public abstract class AbstractWidgetProvider extends RoboAppWidgetProvider {
    private static final String TAG = "AbstractWidgetProvider";
    
    private static final HashMap<String,Integer> sIdCache = new HashMap<String,Integer>();

    @Inject TaskPersister mTaskPersister;
    @Inject ProjectPersister mProjectPersister;
    @Inject EntityCache<Project> mProjectCache;
    @Inject ContextPersister mContextPersister;
    @Inject EntityCache<Context> mContextCache;

    @Override
    public void handleReceive(android.content.Context context, Intent intent) {
        super.handleReceive(context, intent);

        String action = intent.getAction();
        if (TaskProvider.UPDATE_INTENT.equals(action) ||
                ProjectProvider.UPDATE_INTENT.equals(action) ||
                ContextProvider.UPDATE_INTENT.equals(action) ||
                ListSettings.LIST_PREFERENCES_UPDATED.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            // Retrieve the identifiers for each instance of your chosen widget.
            ComponentName thisWidget = new ComponentName(context, getClass());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            if (appWidgetIds != null && appWidgetIds.length > 0) {
                this.onUpdate(context, appWidgetManager, appWidgetIds);
            }
        }
    }

    @Override
    public void onUpdate(android.content.Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        ComponentName thisWidget = new ComponentName(context, getClass());
        int[] localAppWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        Arrays.sort(localAppWidgetIds);

        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            if (Arrays.binarySearch(localAppWidgetIds, appWidgetId) >= 0) {
                TaskListContext listContext = WidgetManager.loadListContextPref(context, appWidgetId);
                if (listContext != null) {
                    Log.d(TAG, "Updating widget " + appWidgetId + " with context " + listContext);
                    updateAppWidget(context, appWidgetManager, appWidgetId, listContext);
                } else {
                    Log.e(TAG, "Couldn't build TaskListContext for app widget " + appWidgetId);
                }
            } else {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    String message = String.format("App widget %s not handled by this provider %s", appWidgetId, getClass());
                    Log.d(TAG, message);
                }
            }
        }
    }

    @Override
    public void onDeleted(android.content.Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted");
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        SharedPreferences.Editor editor = Preferences.getEditor(context);
        for (int i=0; i<N; i++) {
            String prefKey = Preferences.getWidgetQueryKey(appWidgetIds[i]);
            editor.remove(prefKey);
        }
        editor.commit();
    }

    @Override
    public void onEnabled(android.content.Context context) {
    }

    @Override
    public void onDisabled(android.content.Context context) {
    }

    private void updateAppWidget(final android.content.Context androidContext, AppWidgetManager appWidgetManager,
            int appWidgetId, TaskListContext listContext) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            String message = String.format("updateAppWidget appWidgetId=%s queryName=%s provider=%s", 
                    appWidgetId, listContext, getClass());
            Log.d(TAG, message);
        }

        RemoteViews views = new RemoteViews(androidContext.getPackageName(), getWidgetLayoutId());

        Cursor taskCursor = createCursor(androidContext, listContext);
        if (taskCursor == null) return;

        String title = listContext.createTitle(androidContext, mContextCache, mProjectCache);
        views.setTextViewText(R.id.title, title + " (" + taskCursor.getCount() + ")");

        setupFrameClickIntents(androidContext, views, listContext);

        int totalEntries = getTotalEntries();
        for (int taskCount = 1; taskCount <= totalEntries; taskCount++) {
            Task task = null;
            Project project = null;
            Context context = null;
            if (taskCursor.moveToNext()) {
                task = mTaskPersister.read(taskCursor);
                project = mProjectCache.findById(task.getProjectId());
                context = mContextCache.findById(task.getContextId());
            }

            int descriptionViewId = updateDescription(androidContext, views, task, taskCount);
            int projectViewId = updateProject(androidContext, views, project, taskCount);
            int contextIconId = updateContext(androidContext, views, context, taskCount);

            if (task != null) {
                Intent intent = IntentUtils.createTaskViewIntent(androidContext, listContext, taskCount - 1);
                PendingIntent pendingIntent = PendingIntent.getActivity(androidContext, 0, intent, 0);
                int entryId = getIdIdentifier(androidContext, "entry_" + taskCount);
                views.setOnClickPendingIntent(entryId, pendingIntent);
                views.setOnClickPendingIntent(descriptionViewId, pendingIntent);
                views.setOnClickPendingIntent(projectViewId, pendingIntent);
                if (contextIconId != 0) {
                    views.setOnClickPendingIntent(contextIconId, pendingIntent);
                }
            }

        }
        taskCursor.close();

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected Cursor createCursor(android.content.Context androidContext, TaskListContext listContext) {
        TaskSelector query = listContext.createSelectorWithPreferences(androidContext);
        return androidContext.getContentResolver().query(
                TaskProvider.Tasks.CONTENT_URI,
                TaskProvider.Tasks.FULL_PROJECTION,
                query.getSelection(androidContext),
                query.getSelectionArgs(),
                query.getSortOrder());
    }

    abstract int getWidgetLayoutId();

    abstract int getTotalEntries();

    protected void setupFrameClickIntents(android.content.Context androidContext, RemoteViews views, TaskListContext listContext){
        Intent intent = IntentUtils.createTaskListIntent(androidContext, listContext);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // just in case intent comes without it
        PendingIntent pendingIntent = PendingIntent.getActivity(androidContext, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.title, pendingIntent);

        intent = IntentUtils.createNewTaskIntent(androidContext, listContext);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingIntent = PendingIntent.getActivity(androidContext, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.add_task, pendingIntent);
    }

    protected int updateDescription(android.content.Context androidContext, RemoteViews views, Task task, int taskCount) {
        int descriptionViewId = getIdIdentifier(androidContext, "description_" + taskCount);
        if (descriptionViewId != 0) {
            views.setTextViewText(descriptionViewId, task != null ? task.getDescription() : "");
        }

        return descriptionViewId;
    }

    protected int updateProject(android.content.Context androidContext, RemoteViews views, Project project, int taskCount) {
        int projectViewId = getIdIdentifier(androidContext, "project_" + taskCount);
        views.setViewVisibility(projectViewId, project == null ? View.INVISIBLE : View.VISIBLE);
        views.setTextViewText(projectViewId, project != null ? project.getName() : "");

        return projectViewId;
    }

    abstract protected int updateContext(android.content.Context androidContext, RemoteViews views, Context context, int taskCount);



    static int getIdIdentifier(android.content.Context context, String name) {
        Integer id = sIdCache.get(name);
        if (id == null) {
            id = getIdentifier(context, name, cIdType);
            if (id == 0) return id;
            sIdCache.put(name, id);
        }
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Got id " + id + " for resource " + name);
        }
        return id;
    }

    static int getIdentifier(android.content.Context context, String name, String type) {
        int id = context.getResources().getIdentifier(
                    name, type, cPackage);
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Got id " + id + " for resource " + name);
        }
        return id;
    }

}
