package org.dodgybits.shuffle.gwt.core;

import java.util.Date;
import java.util.List;

import org.dodgybits.shuffle.gwt.formatter.ActionDateFormatter;
import org.dodgybits.shuffle.shared.TaskProxy;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class InboxView extends ViewImpl implements InboxPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, InboxView> {
	}

	private ActionDateFormatter mFormatter;

	@UiField
	FlexTable table;

	@Inject
	public InboxView(final Binder binder) {
		widget = binder.createAndBindUi(this);
		
		mFormatter = new ActionDateFormatter();
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void displayTasks(List<TaskProxy> tasks) {
		int numActions = tasks.size();
		for (int i = 0; i < numActions; i++) {
			TaskProxy taskValue = tasks.get(i);
			displayAction(taskValue, i);
		}
	}

	private void displayAction(TaskProxy taskValue, int row) {
		String description = "<div class='actionTitle'>"
				+ escapeHtml(taskValue.getDescription())
				+ "<span class='actionDetails'> - "
				+ escapeHtml(taskValue.getDetails()) + "</span></div>";
		table.setHTML(row, 0, description);

		table.setText(row, 1,
				mFormatter.getShortDate(taskValue.getModifiedDate()));
		table.getCellFormatter().setStyleName(
				row,
				1,
				isInPast(taskValue.getModifiedDate()) ? "actionDueInPass"
						: "actionDueInFuture");
	}

	private static String escapeHtml(String maybeHtml) {
		final Element div = DOM.createDiv();
		DOM.setInnerText(div, maybeHtml);
		return DOM.getInnerHTML(div);
	}

	private static boolean isInPast(Date date) {
		return date != null && date.getTime() < System.currentTimeMillis();
	}

}
