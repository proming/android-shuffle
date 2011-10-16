package org.dodgybits.shuffle.gwt.core;

import org.dodgybits.shuffle.gwt.place.NameTokens;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

public class NavigationPresenter extends
		PresenterWidget<NavigationPresenter.MyView> implements NavigationUiHandlers {

	public interface MyView extends View, HasUiHandlers<NavigationUiHandlers> {
		// TODO Put your view methods here
	}

	
	private PlaceManager placeManager;
	
	@Inject
	public NavigationPresenter(final EventBus eventBus, final MyView view, PlaceManager placeManager) {
		super(eventBus, view);
		
		this.placeManager = placeManager;
	    getView().setUiHandlers(this);
	}

	public void onNewAction() {
		PlaceRequest myRequest = new PlaceRequest(NameTokens.editAction);
		placeManager.revealPlace( myRequest ); 
	}
}
