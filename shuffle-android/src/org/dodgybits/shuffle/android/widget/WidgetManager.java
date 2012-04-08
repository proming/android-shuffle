/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.view.task.TaskListContext;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import roboguice.inject.ContextScopedProvider;
import roboguice.inject.ContextSingleton;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that maintains references to all widgets.
 */
@ContextSingleton
public class WidgetManager {
    private static final String TAG = "WidgetManager";

    // Widget ID -> Widget
    private final static Map<Integer, TaskWidget> mWidgets =
            new ConcurrentHashMap<Integer, TaskWidget>();

    @Inject
    private ContextScopedProvider<TaskWidget> mTaskWidgetProvider;

    public synchronized void createWidgets(Context context, int[] widgetIds) {
        for (int widgetId : widgetIds) {
            getOrCreateWidget(context, widgetId);
        }
    }

    public synchronized void deleteWidgets(Context context, int[] widgetIds) {
        for (int widgetId : widgetIds) {
            // Find the widget in the map
            final TaskWidget widget = get(widgetId);
            if (widget != null) {
                // Stop loading and remove the widget from the map
                widget.onDeleted();
            }
            remove(context, widgetId);
        }
    }

    public synchronized void updateWidgets(Context context, int[] widgetIds) {
        for (int widgetId : widgetIds) {
            // Find the widget in the map
            final TaskWidget widget = get(widgetId);
            if (widget != null) {
                widget.reset();
            } else {
                getOrCreateWidget(context, widgetId);
            }
        }
    }

    public synchronized TaskWidget getOrCreateWidget(Context context, int widgetId) {
        TaskWidget widget = get(widgetId);
        if (widget == null) {
            Log.d(TAG, "Create email widget; ID: " + widgetId);
            widget = mTaskWidgetProvider.get(context);
            widget.setWidgetId(widgetId);
            put(widgetId, widget);
            widget.start();
        }
        return widget;
    }

    private TaskWidget get(int widgetId) {
        return mWidgets.get(widgetId);
    }

    private void put(int widgetId, TaskWidget widget) {
        mWidgets.put(widgetId, widget);
    }

    private void remove(Context context, int widgetId) {
        mWidgets.remove(widgetId);
        WidgetManager.removeWidgetPrefs(context, widgetId);
    }

    public static void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        int n = 0;
        for (TaskWidget widget : mWidgets.values()) {
            writer.println("Widget #" + (++n));
            writer.println("    " + widget.toString());
        }
    }

    /** Saves shared preferences for the given widget */
    static void saveWidgetPrefs(Context context, int appWidgetId, TaskListContext listContext) {
        String queryKey = Preferences.getWidgetQueryKey(appWidgetId);
        String contextIdKey = Preferences.getWidgetContextIdKey(appWidgetId);
        String projectIdKey = Preferences.getWidgetProjectIdKey(appWidgetId);
        TaskSelector selector = listContext.createSelectorWithPreferences(context);
        Preferences.getEditor(context).
                putString(queryKey, listContext.getListQuery().name()).
                putLong(contextIdKey, selector.getContextId().getId()).
                putLong(projectIdKey, selector.getProjectId().getId()).
                commit();
    }

    /** Removes shared preferences for the given widget */
    static void removeWidgetPrefs(Context context, int appWidgetId) {
        String queryKey = Preferences.getWidgetQueryKey(appWidgetId);
        String contextIdKey = Preferences.getWidgetContextIdKey(appWidgetId);
        String projectIdKey = Preferences.getWidgetProjectIdKey(appWidgetId);
        SharedPreferences.Editor editor = Preferences.getEditor(context);
        editor.remove(queryKey).
                remove(contextIdKey).
                remove(projectIdKey).
                apply(); // just want to clean up; don't care when preferences are actually removed
    }

    /**
     * Returns the saved list context for the given widget.
     */
    static TaskListContext loadListContextPref(Context context, int appWidgetId) {
        TaskListContext listContext = null;
        String contextIdKey = Preferences.getWidgetContextIdKey(appWidgetId);
        Id contextId = Preferences.getWidgetId(context, contextIdKey);
        String projectIdKey = Preferences.getWidgetProjectIdKey(appWidgetId);
        Id projectId = Preferences.getWidgetId(context, projectIdKey);
        String queryKey = Preferences.getWidgetQueryKey(appWidgetId);
        String queryName = Preferences.getWidgetQuery(context, queryKey);
        if (queryName != null) {
            queryName = convertOldKeys(context, appWidgetId, queryKey, queryName);
            ListQuery query = ListQuery.valueOf(queryName);
            listContext = TaskListContext.create(query, contextId, projectId);
        }
        return listContext;
    }

    /**
     * Convert old keys from previous versions of Shuffle.
     */
    private static String convertOldKeys(Context context, int appWidgetId, String queryKey, String queryName) {
        String newKey = null;

        if ("due_today".equals(queryName)) {
            newKey = ListQuery.dueToday.name();
        } else if ("next_tasks".equals(queryName)) {
            newKey = ListQuery.nextTasks.name();
        }

        if (newKey == null) {
            newKey = queryName;
        } else {
            Preferences.getEditor(context).putString(queryKey, newKey).commit();
        }

        return newKey;
    }

}
