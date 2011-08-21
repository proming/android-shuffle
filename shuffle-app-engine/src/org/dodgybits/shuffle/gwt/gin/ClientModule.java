package org.dodgybits.shuffle.gwt.gin;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import org.dodgybits.shuffle.gwt.core.ErrorPresenter;
import org.dodgybits.shuffle.gwt.core.ErrorView;
import org.dodgybits.shuffle.gwt.core.HelpPresenter;
import org.dodgybits.shuffle.gwt.core.HelpView;
import org.dodgybits.shuffle.gwt.core.LoginPresenter;
import org.dodgybits.shuffle.gwt.core.LoginView;
import org.dodgybits.shuffle.gwt.core.MainPresenter;
import org.dodgybits.shuffle.gwt.core.MainView;
import org.dodgybits.shuffle.gwt.core.WelcomePresenter;
import org.dodgybits.shuffle.gwt.core.WelcomeView;
import org.dodgybits.shuffle.gwt.place.ClientPlaceManager;
import org.dodgybits.shuffle.gwt.place.DefaultPlace;
import org.dodgybits.shuffle.gwt.place.ErrorPlace;
import org.dodgybits.shuffle.gwt.place.NameTokens;

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
	}

}
