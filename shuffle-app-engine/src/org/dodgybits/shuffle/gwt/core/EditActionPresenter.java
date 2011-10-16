package org.dodgybits.shuffle.gwt.core;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import org.dodgybits.shuffle.gwt.place.NameTokens;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.google.inject.Inject;
import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.dodgybits.shuffle.gwt.core.MainPresenter;

public class EditActionPresenter extends
		Presenter<EditActionPresenter.MyView, EditActionPresenter.MyProxy> {

	public interface MyView extends View {
		// TODO Put your view methods here
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.editAction)
	public interface MyProxy extends ProxyPlace<EditActionPresenter> {
	}

	@Inject
	public EditActionPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPresenter.MAIN_SLOT, this);
	}
}
