package org.dodgybits.shuffle.gwt.core;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;
import org.dodgybits.shuffle.gwt.place.NameTokens;

public class ErrorPresenter extends
		Presenter<ErrorPresenter.MyView, ErrorPresenter.MyProxy> {

	public interface MyView extends View {
		// TODO Put your view methods here
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.error)
	public interface MyProxy extends ProxyPlace<ErrorPresenter> {
	}

	@Inject
	public ErrorPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
	}

	@Override
	protected void revealInParent() {
		RevealRootLayoutContentEvent.fire(this, this);
	}
}
