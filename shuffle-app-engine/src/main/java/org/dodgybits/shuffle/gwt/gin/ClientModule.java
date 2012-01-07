package org.dodgybits.shuffle.gwt.gin;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import org.dodgybits.shuffle.client.ShuffleRequestFactory;
import org.dodgybits.shuffle.gwt.core.*;
import org.dodgybits.shuffle.gwt.cursor.ContextEntityCache;
import org.dodgybits.shuffle.gwt.cursor.ProjectEntityCache;
import org.dodgybits.shuffle.gwt.cursor.TaskNavigator;
import org.dodgybits.shuffle.gwt.place.ClientPlaceManager;
import org.dodgybits.shuffle.gwt.place.DefaultPlace;
import org.dodgybits.shuffle.gwt.place.ErrorPlace;
import org.dodgybits.shuffle.gwt.place.NameTokens;
import org.dodgybits.shuffle.gwt.settings.RestoreFromBackupPresenter;
import org.dodgybits.shuffle.gwt.settings.RestoreFromBackupView;
import org.dodgybits.shuffle.shared.ContextService;
import org.dodgybits.shuffle.shared.ProjectService;
import org.dodgybits.shuffle.shared.TaskService;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		install(new DefaultModule(ClientPlaceManager.class));

		bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.inbox);

		bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.error);

        bindPresenter(ErrorPresenter.class, ErrorPresenter.MyView.class,
                ErrorView.class, ErrorPresenter.MyProxy.class);

		bindPresenter(MainPresenter.class, MainPresenter.MyView.class,
				MainView.class, MainPresenter.MyProxy.class);

		bindPresenter(InboxPresenter.class, InboxPresenter.MyView.class,
				InboxView.class, InboxPresenter.MyProxy.class);

		bindPresenter(EditActionPresenter.class,
				EditActionPresenter.MyView.class, EditActionView.class,
				EditActionPresenter.MyProxy.class);

		bindPresenterWidget(NavigationPresenter.class,
				NavigationPresenter.MyView.class, NavigationView.class);

        bindPresenter(RestoreFromBackupPresenter.class, RestoreFromBackupPresenter.MyView.class,
                RestoreFromBackupView.class, RestoreFromBackupPresenter.MyProxy.class);

		bind(ShuffleRequestFactory.class).in(Singleton.class);
        
        bind(TaskNavigator.class).in(Singleton.class);
        
        bind(ProjectEntityCache.class).in(Singleton.class);
        bind(ContextEntityCache.class).in(Singleton.class);

	}

	@Provides
	TaskService provideTaskService(ShuffleRequestFactory requestFactory) {
		return requestFactory.taskService();
	}

    @Provides
    ContextService provideContextService(ShuffleRequestFactory requestFactory) {
        return requestFactory.contextService();
    }

    @Provides
    ProjectService provideProjectService(ShuffleRequestFactory requestFactory) {
        return requestFactory.projectService();
    }

}
