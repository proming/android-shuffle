package org.dodgybits.shuffle.gwt.gin;

import org.dodgybits.shuffle.client.ShuffleRequestFactory;
import org.dodgybits.shuffle.gwt.core.ErrorPresenter;
import org.dodgybits.shuffle.gwt.core.ErrorView;
import org.dodgybits.shuffle.gwt.core.HelpPresenter;
import org.dodgybits.shuffle.gwt.core.HelpView;
import org.dodgybits.shuffle.gwt.core.InboxPresenter;
import org.dodgybits.shuffle.gwt.core.InboxView;
import org.dodgybits.shuffle.gwt.core.LoginPresenter;
import org.dodgybits.shuffle.gwt.core.LoginView;
import org.dodgybits.shuffle.gwt.core.MainPresenter;
import org.dodgybits.shuffle.gwt.core.MainView;
import org.dodgybits.shuffle.gwt.core.NavigationPresenter;
import org.dodgybits.shuffle.gwt.core.NavigationView;
import org.dodgybits.shuffle.gwt.core.EditActionPresenter;
import org.dodgybits.shuffle.gwt.core.EditActionView;
import org.dodgybits.shuffle.gwt.core.WelcomePresenter;
import org.dodgybits.shuffle.gwt.core.WelcomeView;
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

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;

public class ClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {
		install(new DefaultModule(ClientPlaceManager.class));

		bindPresenter(WelcomePresenter.class, WelcomePresenter.MyView.class,
				WelcomeView.class, WelcomePresenter.MyProxy.class);

		bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.welcome);

		bindPresenter(ErrorPresenter.class, ErrorPresenter.MyView.class,
				ErrorView.class, ErrorPresenter.MyProxy.class);

		bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.error);

		bindPresenter(MainPresenter.class, MainPresenter.MyView.class,
				MainView.class, MainPresenter.MyProxy.class);

		bindPresenter(LoginPresenter.class, LoginPresenter.MyView.class,
				LoginView.class, LoginPresenter.MyProxy.class);

		bindPresenter(HelpPresenter.class, HelpPresenter.MyView.class,
				HelpView.class, HelpPresenter.MyProxy.class);

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
