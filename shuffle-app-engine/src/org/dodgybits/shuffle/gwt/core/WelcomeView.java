package org.dodgybits.shuffle.gwt.core;

import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class WelcomeView extends ViewImpl implements WelcomePresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, WelcomeView> {
	}
	
	@UiField HTMLPanel mainPanel;
	@UiField HTML dateField;
	
	@Inject
	public WelcomeView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setFormattedDate(String formattedDate) {
		dateField.setText(formattedDate);
	}

	@Override
	public void setBackgroundColor(String color) {
		mainPanel.getElement().getStyle().setBackgroundColor(color);
	}
}
