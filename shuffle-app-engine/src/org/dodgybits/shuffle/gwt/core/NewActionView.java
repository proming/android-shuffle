package org.dodgybits.shuffle.gwt.core;

import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class NewActionView extends ViewImpl implements NewActionPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, NewActionView> {
	}

	@Inject
	public NewActionView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}

	@Override
	public Widget asWidget() {
		return widget;
	}
}
