package org.dodgybits.shuffle.gwt.core;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class NavigationView extends ViewWithUiHandlers<NavigationUiHandlers> implements NavigationPresenter.MyView, IsWidget {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, NavigationView> {
	}

	@UiField Button newAction;

	@Inject
	public NavigationView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	  @UiHandler("newAction")
	  void onNewActionButtonClicked(ClickEvent event) {
		if (getUiHandlers() != null) {
			getUiHandlers().onNewAction();
		}
	  }
	
}
