package org.dodgybits.shuffle.gwt.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.gwtplatform.mvp.client.proxy.PlaceManager;

import org.dodgybits.shuffle.client.ShuffleRequestFactory;
import org.dodgybits.shuffle.gwt.core.ErrorPresenter;
import org.dodgybits.shuffle.gwt.core.HelpPresenter;
import org.dodgybits.shuffle.gwt.core.InboxPresenter;
import org.dodgybits.shuffle.gwt.core.LoginPresenter;
import org.dodgybits.shuffle.gwt.core.MainPresenter;
import org.dodgybits.shuffle.gwt.core.NavigationPresenter;
import org.dodgybits.shuffle.gwt.core.EditActionPresenter;
import org.dodgybits.shuffle.gwt.core.WelcomePresenter;
import org.dodgybits.shuffle.gwt.gin.ClientModule;

@GinModules({ DispatchAsyncModule.class, ClientModule.class })
public interface ClientGinjector extends Ginjector {

	EventBus getEventBus();

	PlaceManager getPlaceManager();

	Provider<WelcomePresenter> getWelcomePresenter();

	AsyncProvider<ErrorPresenter> getErrorPresenter();

	Provider<MainPresenter> getMainPresenter();

	AsyncProvider<LoginPresenter> getLoginPresenter();

	AsyncProvider<HelpPresenter> getHelpPresenter();
	
	AsyncProvider<InboxPresenter> getInboxPresenter();

	AsyncProvider<EditActionPresenter> getNewActionPresenter();
	
	Provider<NavigationPresenter> getNavigationPresenter();
	
	ShuffleRequestFactory getRequestFactory();
}
