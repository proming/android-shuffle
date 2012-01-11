package org.dodgybits.shuffle.gwt.core;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import org.dodgybits.shuffle.shared.ContextProxy;
import org.dodgybits.shuffle.shared.ProjectProxy;
import org.dodgybits.shuffle.shared.TaskProxy;

import java.util.List;

public class EditActionView extends ViewWithUiHandlers<EditEntityUiHandlers> implements EditActionPresenter.MyView {

    private final Widget widget;

    public interface Binder extends UiBinder<Widget, EditActionView> {
    }

    @UiField
    Button save;
    @UiField
    Button cancel;

    @UiField
    Button previous;
    @UiField
    Button next;

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

    @Override
    public void displayTask(TaskProxy task) {
        description.setText(task.getDescription());
        details.setText(task.getDetails());
        from.setValue(task.getShowFromDate());
        due.setValue(task.getDueDate());

        displayProject(task.getProject());
        displayContexts(task.getContexts());
    }

    @Override
    public void displayProject(ProjectProxy project) {
        String name = "";
        if (project != null) {
            name = project.getName();
        }
        this.project.setText(name);
    }

    @Override
    public void displayContexts(List<ContextProxy> contexts) {
        String name = "";
        if (!contexts.isEmpty()) {
            name = contexts.get(0).getName();
        }
        this.context.setText(name);
    }

    @UiHandler("save")
    void onSaveButtonClicked(ClickEvent event) {
        GWT.log("Saving task");
        getUiHandlers().save(description.getText(), details.getText());
    }

    @UiHandler("cancel")
    void onCancelButtonClicked(ClickEvent event) {
        GWT.log("Canceling task edit");
        getUiHandlers().cancel();
    }

    @UiHandler("previous")
    void onPreviousButtonClicked(ClickEvent event) {
        GWT.log("Previous task");
        getUiHandlers().previous();
    }

    @UiHandler("next")
    void onNextButtonClicked(ClickEvent event) {
        GWT.log("Next task");
        getUiHandlers().next();
    }

}
