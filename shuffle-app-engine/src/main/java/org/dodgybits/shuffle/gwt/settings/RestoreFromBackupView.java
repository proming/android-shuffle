package org.dodgybits.shuffle.gwt.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class RestoreFromBackupView extends ViewImpl implements RestoreFromBackupPresenter.MyView {

    private final Widget widget;

    public interface Binder extends UiBinder<Widget, RestoreFromBackupView> {
    }

    @UiField
    FormPanel form;

    @UiField
    Button submit;

    @Inject
    public RestoreFromBackupView(final Binder binder) {
        widget = binder.createAndBindUi(this);

        form.setAction("/restoreBackup");

        form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {

            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                Window.alert(event.getResults());
            }
        });

    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @UiHandler("submit")
    void onSaveButtonClicked(ClickEvent event) {
        GWT.log("Submitting backup file ");
        form.submit();
    }

}
