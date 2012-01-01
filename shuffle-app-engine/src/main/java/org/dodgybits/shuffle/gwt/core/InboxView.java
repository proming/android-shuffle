package org.dodgybits.shuffle.gwt.core;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import org.dodgybits.shuffle.gwt.formatter.ActionDateFormatter;
import org.dodgybits.shuffle.shared.ContextProxy;
import org.dodgybits.shuffle.shared.ProjectProxy;
import org.dodgybits.shuffle.shared.TaskProxy;

import java.util.Date;
import java.util.List;

public class InboxView extends ViewWithUiHandlers<TaskListUiHandlers> implements InboxPresenter.MyView {

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

    @UiField(provided = true)
    SimplePager pager;

    @UiField(provided = true)
    DataGrid<TaskProxy> grid;

    @Inject
    public InboxView(final Binder binder) {
        mFormatter = new ActionDateFormatter();

        // Create a grid.

        // Set a key provider that provides a unique key for each contact. If key is
        // used to identify contacts when fields (such as the name and address)
        // change.
        grid = new DataGrid<TaskProxy>(KEY_PROVIDER);
        grid.setWidth("100%");

        // Create a Pager to control the table.
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(grid);

        // Add a selection model so we can select cells.
        final SelectionModel<TaskProxy> selectionModel = new MultiSelectionModel<TaskProxy>(KEY_PROVIDER);
        grid.setSelectionModel(selectionModel,
                DefaultSelectionEventManager.<TaskProxy>createCheckboxManager());

        // Initialize the columns.
        initTableColumns(selectionModel);

        widget = binder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setUiHandlers(TaskListUiHandlers uiHandlers) {
        super.setUiHandlers(uiHandlers);
        if (getUiHandlers() != null) {
            getUiHandlers().setDisplay(grid);
        }
    }

    @Override
    public void redraw() {
        grid.redraw();
    }

    /**
     * Add the columns to the grid.
     */
    private void initTableColumns(
            final SelectionModel<TaskProxy> selectionModel) {

        // Checkbox column. This grid will uses a checkbox column for selection.
        // Alternatively, you can call grid.setSelectionEnabled(true) to enable
        // mouse selection.
        Column<TaskProxy, Boolean> checkColumn = new Column<TaskProxy, Boolean>(
                new CheckboxCell(true, false)) {
            @Override
            public Boolean getValue(TaskProxy object) {
                // Get the value from the selection model.
                return selectionModel.isSelected(object);
            }
        };
        grid.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        grid.setColumnWidth(checkColumn, 3, Unit.EM);

        // Details.
        Column<TaskProxy, String> detailsColumn = new Column<TaskProxy, String>(
                new ClickableTextCell(new SafeHtmlRenderer<String>() {

                    public SafeHtml render(String object) {
                        return (object == null) ? SafeHtmlUtils.EMPTY_SAFE_HTML : SafeHtmlUtils.fromTrustedString(object);
                    }

                    public void render(String object, SafeHtmlBuilder appendable) {
                        appendable.append(SafeHtmlUtils.fromTrustedString(object));
                    }
                })) {
            @Override
            public String getValue(TaskProxy taskValue) {
                String contextNames = " (";
                List<ContextProxy> contexts = getUiHandlers().getContexts(taskValue);
                for (ContextProxy context : contexts) {
                    contextNames += context == null ? "" : context.getName();
                }
                contextNames += ") ";

                String projectName = "";
                ProjectProxy project = getUiHandlers().getProject(taskValue);
                if (project != null) {
                    projectName = "<b>" + project.getName() + "</b>";
                }

                String description = "<div class='action-title'>"
                        + SafeHtmlUtils.htmlEscape(taskValue.getDescription())
                        + "<span class='action-details'> - "
                        + SafeHtmlUtils.htmlEscape(taskValue.getDetails()) + "</span>"
                        + contextNames + projectName + "</div>";
                return description;
            }
        };
        detailsColumn.setFieldUpdater(new FieldUpdater<TaskProxy, String>() {
            @Override
            public void update(int index, TaskProxy object, String value) {
                if (getUiHandlers() != null) {
                    getUiHandlers().onEditAction(index, object);
                }
            }
        }


        );
        grid.addColumn(detailsColumn, "Details");
        grid.setColumnWidth(detailsColumn, 80, Unit.PCT);


        // Date.
        Column<TaskProxy, String> dueDateColumn = new Column<TaskProxy, String>(
                new TextCell()) {
            @Override
            public String getValue(TaskProxy taskValue) {
                return mFormatter.getShortDate(taskValue.getModifiedDate());
            }
        };
        dueDateColumn.setSortable(true);
        grid.addColumn(dueDateColumn, "Due");
        grid.setColumnWidth(dueDateColumn, 8, Unit.EM);
    }

//	private void displayAction(TaskProxy taskValue, int row) {
//		String description = "<div class='actionTitle'>"
//				+ escapeHtml(taskValue.getDescription())
//				+ "<span class='actionDetails'> - "
//				+ escapeHtml(taskValue.getDetails()) + "</span></div>";
//		grid.setHTML(row, 0, description);
//
//		grid.setText(row, 1,
//				mFormatter.getShortDate(taskValue.getModifiedDate()));
//		grid.getCellFormatter().setStyleName(
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
