/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.shuffle.android.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import org.dodgybits.shuffle.android.roboguice.RoboAppWidgetProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.dodgybits.shuffle.android.core.util.Constants.cIdType;
import static org.dodgybits.shuffle.android.core.util.Constants.cPackage;

/**
 * A widget provider.  We have a string that we pull from a preference in order to show
 * the configuration settings and the current time when the widget was updated.  We also
 * register a BroadcastReceiver for time-changed and timezone-changed broadcasts, and
 * update then too.
 */
public class PreHoneycombWidgetProvider extends RoboAppWidgetProvider {
    private static final String TAG = "PreHCWidgetProvider";
    private static final int ENTRIES = 7;

    private static final HashMap<String,Integer> sIdCache = new HashMap<String,Integer>();

    private ContextBitmapProvider mBitmapProvider;

    @Inject
    TaskPersister mTaskPersister;
    @Inject
    ProjectPersister mProjectPersister;
    @Inject
    EntityCache<Project> mProjectCache;
    @Inject
    ContextPersister mContextPersister;
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
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "Updating widget " + appWidgetId + " with context " + listContext);
                    }
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

    private void updateAppWidget(final android.content.Context androidContext, AppWidgetManager appWidgetManager,
                                 int appWidgetId, TaskListContext listContext) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            String message = String.format("updateAppWidget appWidgetId=%s queryName=%s provider=%s",
                    appWidgetId, listContext, getClass());
            Log.d(TAG, message);
        }

        RemoteViews views = new RemoteViews(androidContext.getPackageName(), R.layout.widget_pre_honeycomb);

        Cursor taskCursor = createCursor(androidContext, listContext);
        if (taskCursor == null) return;

        String title = listContext.createTitle(androidContext, mContextCache, mProjectCache);
        views.setTextViewText(R.id.widget_title, title);
        views.setTextViewText(R.id.widget_count, String.valueOf(taskCursor.getCount()));

        setupFrameClickIntents(androidContext, views, listContext);

        for (int taskCount = 1; taskCount <= ENTRIES; taskCount++) {
            Task task = null;
            Project project = null;
            List<Context> contexts = Collections.emptyList();
            if (taskCursor.moveToNext()) {
                task = mTaskPersister.read(taskCursor);
                project = mProjectCache.findById(task.getProjectId());
                contexts = mContextCache.findById(task.getContextIds());
            }

            int entryId = updateBackground(androidContext, views, task, taskCount);
            int descriptionViewId = updateDescription(androidContext, views, task, taskCount);
            int projectViewId = updateProject(androidContext, views, project, taskCount);
            updateContexts(androidContext, views, contexts, taskCount);

            if (task != null) {
                Intent intent = IntentUtils.createTaskViewIntent(androidContext, listContext, taskCount - 1);

                // need to add data to it so they don't all get treated the same...
                Uri.Builder builder = TaskProvider.Tasks.CONTENT_URI.buildUpon();
                ContentUris.appendId(builder, task.getLocalId().getId());
                Uri taskUri = builder.build();
                intent.setData(taskUri);

                setActivityIntent(androidContext, views, entryId, intent);
                setActivityIntent(androidContext, views, descriptionViewId, intent);
                setActivityIntent(androidContext, views, projectViewId, intent);
            }

        }
        taskCursor.close();

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private Cursor createCursor(android.content.Context androidContext, TaskListContext listContext) {
        TaskSelector query = listContext.createSelectorWithPreferences(androidContext);
        return androidContext.getContentResolver().query(
                TaskProvider.Tasks.CONTENT_URI,
                TaskProvider.Tasks.FULL_PROJECTION,
                query.getSelection(androidContext),
                query.getSelectionArgs(),
                query.getSortOrder());
    }

    /**
     * Convenience method for creating an onClickPendingIntent that launches another activity
     * directly.
     *
     * @param views The RemoteViews we're inflating
     * @param buttonId the id of the button view
     * @param intent The intent to be used when launching the activity
     */
    private void setActivityIntent(android.content.Context androidContext, RemoteViews views, int buttonId, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // just in case intent comes without it
        PendingIntent pendingIntent =
                PendingIntent.getActivity(androidContext, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(buttonId, pendingIntent);
    }

    private void setupFrameClickIntents(android.content.Context androidContext, RemoteViews views, TaskListContext listContext){
        Intent intent = IntentUtils.createTaskListIntent(androidContext, listContext);
        setActivityIntent(androidContext, views, R.id.widget_title, intent);
        setActivityIntent(androidContext, views, R.id.widget_logo, intent);

        intent = IntentUtils.createNewTaskIntent(androidContext, listContext);
        setActivityIntent(androidContext, views, R.id.widget_compose, intent);
    }

    private int updateBackground(android.content.Context androidContext, RemoteViews views, Task task, int taskCount) {
        int entryId = getIdIdentifier(androidContext, "entry_" + taskCount);
        if (entryId != 0) {
            int drawableId = R.drawable.list_selector_background;
            if (task != null) {
                boolean isIncomplete = !(task.isComplete() || task.isDeleted());
                drawableId = R.drawable.task_complete_selector;
                if (isIncomplete) {
                    drawableId = R.drawable.task_incomplete_selector;
                }
            }
            views.setInt(entryId, "setBackgroundResource", drawableId);
        }
        return entryId;
    }

    private int updateDescription(android.content.Context androidContext, RemoteViews views, Task task, int taskCount) {
        int descriptionViewId = getIdIdentifier(androidContext, "description_" + taskCount);
        if (descriptionViewId != 0) {
            views.setTextViewText(descriptionViewId, task != null ? task.getDescription() : "");
        }

        return descriptionViewId;
    }

    private void updateContexts(android.content.Context androidContext,
                                RemoteViews views, List<Context> contexts, int taskCount) {
        if (mBitmapProvider == null) {
            mBitmapProvider = new ContextBitmapProvider(androidContext);
        }
        int contextViewId = getIdIdentifier(androidContext, "contextColour_" + taskCount);
        views.setImageViewBitmap(contextViewId, mBitmapProvider.getBitmapForContexts(contexts));
    }


    private int updateProject(android.content.Context androidContext, RemoteViews views, Project project, int taskCount) {
        int projectViewId = getIdIdentifier(androidContext, "project_" + taskCount);
        views.setViewVisibility(projectViewId, project == null ? View.INVISIBLE : View.VISIBLE);
        views.setTextViewText(projectViewId, project != null ? project.getName() : "");

        return projectViewId;
    }

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



