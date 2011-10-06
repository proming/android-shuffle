package org.dodgybits.shuffle.gwt.core;

import java.util.Date;
import java.util.List;

import org.dodgybits.shuffle.gwt.formatter.ActionDateFormatter;
import org.dodgybits.shuffle.shared.TaskProxy;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class InboxView extends ViewImpl implements InboxPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, InboxView> {
	}
	
	  private static final ProvidesKey<TaskProxy> KEY_PROVIDER =
		      new ProvidesKey<TaskProxy>() {
		        @Override
		        public Object getKey(TaskProxy item) {
		          return item.getId();
		        }
		      };
	

	private ActionDateFormatter mFormatter;

	@UiField(provided=true)
	CellTable<TaskProxy> table;

	  /**
	   * The provider that holds the list of contacts in the database.
	   */
	  private ListDataProvider<TaskProxy> dataProvider = new ListDataProvider<TaskProxy>();
	
	@Inject
	public InboxView(final Binder binder) {
		mFormatter = new ActionDateFormatter();

		// Create a table.

	    // Set a key provider that provides a unique key for each contact. If key is
	    // used to identify contacts when fields (such as the name and address)
	    // change.
	    table = new CellTable<TaskProxy>(KEY_PROVIDER);
	    table.setWidth("100%", true);

	    // Add a selection model so we can select cells.
	    final SelectionModel<TaskProxy> selectionModel = new MultiSelectionModel<TaskProxy>(KEY_PROVIDER);
	    table.setSelectionModel(selectionModel,
	        DefaultSelectionEventManager.<TaskProxy> createCheckboxManager());

	    // Initialize the columns.
	    initTableColumns(selectionModel);

	    widget = binder.createAndBindUi(this);
	  }
	
	  /**
	   * Add the columns to the table.
	   */
	  private void initTableColumns(
	      final SelectionModel<TaskProxy> selectionModel) {
		  
	    // Checkbox column. This table will uses a checkbox column for selection.
	    // Alternatively, you can call table.setSelectionEnabled(true) to enable
	    // mouse selection.
	    Column<TaskProxy, Boolean> checkColumn = new Column<TaskProxy, Boolean>(
	        new CheckboxCell(true, false)) {
	      @Override
	      public Boolean getValue(TaskProxy object) {
	        // Get the value from the selection model.
	        return selectionModel.isSelected(object);
	      }
	    };
	    table.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
	    table.setColumnWidth(checkColumn, 40, Unit.PX);

	    // Details.
	    Column<TaskProxy, String> detailsColumn = new Column<TaskProxy, String>(
	        new EditTextCell(new SafeHtmlRenderer<String>() {
				
	        	  public SafeHtml render(String object) {
	        		    return (object == null) ? SafeHtmlUtils.EMPTY_SAFE_HTML : SafeHtmlUtils.fromTrustedString(object);
	        		  }

	        		  public void render(String object, SafeHtmlBuilder appendable) {
	        		    appendable.append(SafeHtmlUtils.fromTrustedString(object));
	        		  }
			})) {
	      @Override
	      public String getValue(TaskProxy taskValue) {
	  		String description = "<div class='actionTitle'>"
	  				+ SafeHtmlUtils.htmlEscape(taskValue.getDescription())
				+ "<span class='actionDetails'> - "
				+ SafeHtmlUtils.htmlEscape(taskValue.getDetails()) + "</span></div>";
	        return description;
	      }
	    };
	    table.addColumn(detailsColumn, "Details");
	    table.setColumnWidth(detailsColumn, 80, Unit.PCT);


	    // Date.
	    Column<TaskProxy, String> dueDateColumn = new Column<TaskProxy, String>(
	        new TextCell()) {
	      @Override
	      public String getValue(TaskProxy taskValue) {
	        return mFormatter.getShortDate(taskValue.getModifiedDate());
	      }
	    };
	    dueDateColumn.setSortable(true);
	    table.addColumn(dueDateColumn, "Due");
	    table.setColumnWidth(dueDateColumn, 60, Unit.PCT);
	  }

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void displayTasks(List<TaskProxy> tasks) {
		dataProvider.setList(tasks);
		dataProvider.addDataDisplay(table);
	}

//	private void displayAction(TaskProxy taskValue, int row) {
//		String description = "<div class='actionTitle'>"
//				+ escapeHtml(taskValue.getDescription())
//				+ "<span class='actionDetails'> - "
//				+ escapeHtml(taskValue.getDetails()) + "</span></div>";
//		table.setHTML(row, 0, description);
//
//		table.setText(row, 1,
//				mFormatter.getShortDate(taskValue.getModifiedDate()));
//		table.getCellFormatter().setStyleName(
//				row,
//				1,
//				isInPast(taskValue.getModifiedDate()) ? "actionDueInPass"
//						: "actionDueInFuture");
//	}
//
//	private static String escapeHtml(String maybeHtml) {
//		final Element div = DOM.createDiv();
//		DOM.setInnerText(div, maybeHtml);
//		return DOM.getInnerHTML(div);
//	}

	private static boolean isInPast(Date date) {
		return date != null && date.getTime() < System.currentTimeMillis();
	}

}
