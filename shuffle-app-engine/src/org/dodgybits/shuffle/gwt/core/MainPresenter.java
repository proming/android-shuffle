package org.dodgybits.shuffle.gwt.core;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;

public class MainPresenter extends
		Presenter<MainPresenter.MyView, MainPresenter.MyProxy> {

	public interface MyView extends View {
		// TODO Put your view methods here
	}

	@ProxyStandard
	public interface MyProxy extends Proxy<MainPresenter> {
	}

	@ContentSlot
	public static final Type<RevealContentHandler<?>> MAIN_SLOT = new Type<RevealContentHandler<?>>();
	
	@Inject
	public MainPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
	}

	@Override
	protected void revealInParent() {
		RevealRootLayoutContentEvent.fire(this, this);
	}
}
