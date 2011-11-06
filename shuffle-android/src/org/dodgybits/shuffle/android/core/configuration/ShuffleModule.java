package org.dodgybits.shuffle.android.core.configuration;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.encoding.ContextEncoder;
import org.dodgybits.shuffle.android.core.model.encoding.EntityEncoder;
import org.dodgybits.shuffle.android.core.model.encoding.ProjectEncoder;
import org.dodgybits.shuffle.android.core.model.encoding.TaskEncoder;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.DefaultEntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.Flag;
import org.dodgybits.shuffle.android.core.view.MenuUtils;
import org.dodgybits.shuffle.android.list.annotation.ContextTasks;
import org.dodgybits.shuffle.android.list.annotation.Contexts;
import org.dodgybits.shuffle.android.list.annotation.DueTasks;
import org.dodgybits.shuffle.android.list.annotation.ExpandableContexts;
import org.dodgybits.shuffle.android.list.annotation.ExpandableProjects;
import org.dodgybits.shuffle.android.list.annotation.Inbox;
import org.dodgybits.shuffle.android.list.annotation.ProjectTasks;
import org.dodgybits.shuffle.android.list.annotation.Projects;
import org.dodgybits.shuffle.android.list.annotation.Tickler;
import org.dodgybits.shuffle.android.list.annotation.TopTasks;
import org.dodgybits.shuffle.android.list.config.AbstractTaskListConfig;
import org.dodgybits.shuffle.android.list.config.ContextListConfig;
import org.dodgybits.shuffle.android.list.config.ContextTasksListConfig;
import org.dodgybits.shuffle.android.list.config.DueActionsListConfig;
import org.dodgybits.shuffle.android.list.config.ProjectListConfig;
import org.dodgybits.shuffle.android.list.config.ProjectTasksListConfig;
import org.dodgybits.shuffle.android.list.config.StandardTaskQueries;
import org.dodgybits.shuffle.android.list.config.TaskListConfig;
import org.dodgybits.shuffle.android.preference.model.ListPreferenceSettings;

import android.content.ContextWrapper;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

public class ShuffleModule extends AbstractModule {

    @Override
	protected void configure() {
        addCaches();
        addPersisters();
        addEncoders();
        addListPreferenceSettings();
        addListConfig();
	}

    private void addCaches() {
        bind(new TypeLiteral<EntityCache<Context>>() {}).to(new TypeLiteral<DefaultEntityCache<Context>>() {});
        bind(new TypeLiteral<EntityCache<Project>>() {}).to(new TypeLiteral<DefaultEntityCache<Project>>() {});
    }

    private void addPersisters() {
        bind(new TypeLiteral<EntityPersister<Context>>() {}).to(ContextPersister.class);
        bind(new TypeLiteral<EntityPersister<Project>>() {}).to(ProjectPersister.class);
        bind(new TypeLiteral<EntityPersister<Task>>() {}).to(TaskPersister.class);
    }

    private void addEncoders() {
        bind(new TypeLiteral<EntityEncoder<Context>>() {}).to(ContextEncoder.class);
        bind(new TypeLiteral<EntityEncoder<Project>>() {}).to(ProjectEncoder.class);
        bind(new TypeLiteral<EntityEncoder<Task>>() {}).to(TaskEncoder.class);
    }

    private void addListPreferenceSettings() {
        bind(ListPreferenceSettings.class).annotatedWith(Inbox.class).toInstance(
                new ListPreferenceSettings(StandardTaskQueries.cInbox));

        bind(ListPreferenceSettings.class).annotatedWith(TopTasks.class).toInstance(
                new ListPreferenceSettings(StandardTaskQueries.cNextTasks)
                    .setDefaultCompleted(Flag.no)
                    .disableCompleted()
                    .disableDeleted()
                    .disableActive());

        ListPreferenceSettings projectSettings = new ListPreferenceSettings(StandardTaskQueries.cProjectFilterPrefs);
        bind(ListPreferenceSettings.class).annotatedWith(ProjectTasks.class).toInstance(projectSettings);
        bind(ListPreferenceSettings.class).annotatedWith(Projects.class).toInstance(projectSettings);
        bind(ListPreferenceSettings.class).annotatedWith(ExpandableProjects.class).toInstance(projectSettings);

        ListPreferenceSettings contextSettings = new ListPreferenceSettings(StandardTaskQueries.cContextFilterPrefs);
        bind(ListPreferenceSettings.class).annotatedWith(ContextTasks.class).toInstance(contextSettings);
        bind(ListPreferenceSettings.class).annotatedWith(Contexts.class).toInstance(contextSettings);
        bind(ListPreferenceSettings.class).annotatedWith(ExpandableContexts.class).toInstance(contextSettings);

        bind(ListPreferenceSettings.class).annotatedWith(DueTasks.class).toInstance(
            new ListPreferenceSettings(StandardTaskQueries.cDueTasksFilterPrefs).setDefaultCompleted(Flag.no));

        bind(ListPreferenceSettings.class).annotatedWith(Tickler.class).toInstance(
            new ListPreferenceSettings(StandardTaskQueries.cTickler)
                    .setDefaultCompleted(Flag.no)
                    .setDefaultActive(Flag.no));

    }

    private void addListConfig() {
        bind(DueActionsListConfig.class).annotatedWith(DueTasks.class).to(DueActionsListConfig.class);
        bind(ContextTasksListConfig.class).annotatedWith(ContextTasks.class).to(ContextTasksListConfig.class);
        bind(ProjectTasksListConfig.class).annotatedWith(ProjectTasks.class).to(ProjectTasksListConfig.class);
        bind(ProjectListConfig.class).annotatedWith(Projects.class).to(ProjectListConfig.class);
        bind(ContextListConfig.class).annotatedWith(Contexts.class).to(ContextListConfig.class);
    }


    @Provides @Inbox
    TaskListConfig providesInboxTaskListConfig(TaskPersister taskPersister, @Inbox ListPreferenceSettings settings) {
		return new AbstractTaskListConfig(
                StandardTaskQueries.getQuery(StandardTaskQueries.cInbox),
                taskPersister, settings) {

		    public int getCurrentViewMenuId() {
		    	return MenuUtils.INBOX_ID;
		    }

		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_inbox);
		    }

		};
    }

    @Provides @TopTasks
    TaskListConfig providesTopTasksTaskListConfig(TaskPersister taskPersister, @TopTasks ListPreferenceSettings settings) {
        return new AbstractTaskListConfig(
                StandardTaskQueries.getQuery(StandardTaskQueries.cNextTasks),
                taskPersister, settings) {

		    public int getCurrentViewMenuId() {
		    	return MenuUtils.TOP_TASKS_ID;
		    }

		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_next_tasks);
		    }

		};
    }

    @Provides @Tickler
    TaskListConfig providesTicklerTaskListConfig(TaskPersister taskPersister, @Tickler ListPreferenceSettings settings) {
        return new AbstractTaskListConfig(
                StandardTaskQueries.getQuery(StandardTaskQueries.cTickler),
                taskPersister, settings) {

		    public int getCurrentViewMenuId() {
		    	return MenuUtils.INBOX_ID;
		    }

		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_tickler);
		    }

		};
    }



}
