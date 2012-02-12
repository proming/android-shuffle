package org.dodgybits.shuffle.android.list.listener;

import android.app.Activity;
import android.content.Intent;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.core.activity.HelpActivity;
import org.dodgybits.shuffle.android.list.event.*;
import org.dodgybits.shuffle.android.list.model.ListSettingsCache;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.preference.activity.PreferencesActivity;
import roboguice.event.Observes;

public class NavigationListener {

    private Activity mActivity;

    @Inject
    public NavigationListener(Activity activity) {
        mActivity = activity;
    }

    public void onViewPreferences(@Observes ViewPreferencesEvent event) {
        Intent intent = new Intent(mActivity, PreferencesActivity.class);
        mActivity.startActivity(intent);
    }

    public void onViewHelp(@Observes ViewHelpEvent event) {
        Intent intent = new Intent(mActivity, HelpActivity.class);
        if (event.getListQuery() != null) {
            intent.putExtra(HelpActivity.LIST_QUERY, event.getListQuery().name());
        }
        mActivity.startActivity(intent);
    }

    public void onEditListSettings(@Observes EditListSettingsEvent event) {
        Intent intent = ListSettingsCache.createListSettingsEditorIntent(mActivity, event.getListQuery());
        mActivity.startActivityForResult(intent, event.getRequestCode());
    }

    public void onNewTask(@Observes NewTaskEvent event) {
        Intent intent = new Intent(Intent.ACTION_INSERT, TaskProvider.Tasks.CONTENT_URI);
        if (event.getContextId().isInitialised()) {
            intent.putExtra(TaskProvider.Tasks.CONTEXT_ID, event.getContextId().getId());
        }
        if (event.getProjectId().isInitialised()) {
            intent.putExtra(TaskProvider.Tasks.PROJECT_ID, event.getProjectId().getId());
        }
        mActivity.startActivity(intent);
    }

    public void onNewProject(@Observes NewProjectEvent event) {
        Intent intent = new Intent(Intent.ACTION_INSERT, ProjectProvider.Projects.CONTENT_URI);
        mActivity.startActivity(intent);
    }

    public void onNewContext(@Observes NewContextEvent event) {
        Intent intent = new Intent(Intent.ACTION_INSERT, ContextProvider.Contexts.CONTENT_URI);
        mActivity.startActivity(intent);
    }
}
