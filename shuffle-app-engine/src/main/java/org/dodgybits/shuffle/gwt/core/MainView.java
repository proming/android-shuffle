package org.dodgybits.shuffle.gwt.core;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class MainView extends ViewImpl implements MainPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, MainView> {
	}
	
	@UiField HTMLPanel mainPanel;
	@UiField HTMLPanel navigation;
	
	@Inject
	public MainView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	@Override
	public void setInSlot(Object slot, Widget widget) {
		if (slot == MainPresenter.MAIN_SLOT) {
			mainPanel.clear();
			mainPanel.add(widget);
		} else if (slot == MainPresenter.NAVIGATION_SLOT) {
			navigation.clear();
			navigation.add(widget);
		}
	}
}
