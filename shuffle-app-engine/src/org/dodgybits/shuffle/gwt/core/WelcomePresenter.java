package org.dodgybits.shuffle.gwt.core;

import org.dodgybits.shuffle.gwt.place.NameTokens;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootLayoutContentEvent;

public class WelcomePresenter extends
		Presenter<WelcomePresenter.MyView, WelcomePresenter.MyProxy> {

	public interface MyView extends View {
		public void setFormattedDate(String formattedDate);

		public void setBackgroundColor(String color);
	}

	@ProxyStandard
	@NameToken(NameTokens.welcome)
	public interface MyProxy extends ProxyPlace<WelcomePresenter> {
	}

	private final DispatchAsync dispatcher;

	@Inject
	public WelcomePresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final DispatchAsync dispatcher) {
		super(eventBus, view, proxy);
		this.dispatcher = dispatcher;
	}

	@Override
	protected void revealInParent() {
		RevealRootLayoutContentEvent.fire(this, this);
	}
	
	@Override
	protected void onReset() {
		getView().setFormattedDate("Loading...");
//		dispatcher.execute(new GetServerDate(), new AsyncCallback<GetServerDateResult>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				getView().setFormattedDate("An error occurred!");				
//			}
//			@Override
//			public void onSuccess(GetServerDateResult result) {
//				getView().setFormattedDate(result.getFormattedDate());
//			}
//		});
		super.onReset();
	}
	
	@Override
	public void prepareFromRequest(PlaceRequest request) {
		// Gets the preferred color from a URL parameter, defaults to lightBlue
		String color = request.getParameter("col", "lightBlue");
		getView().setBackgroundColor(color);
	}
}
