package org.dodgybits.shuffle.gwt.core;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class EditActionView extends ViewImpl implements EditActionPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, EditActionView> {
	}

	@UiField Button save;
	
	@UiField
	TextBox description;

	@UiField
	TextBox context;

	@UiField
	TextBox project;

	@UiField
	TextArea details;

	@UiField
	DateBox from;

	@UiField
	DateBox due;
	
	@Inject
	public EditActionView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
}
