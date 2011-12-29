package org.dodgybits.shuffle.gwt.core;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import org.dodgybits.shuffle.shared.ContextProxy;

import java.util.List;

public class NavigationView extends ViewWithUiHandlers<NavigationUiHandlers> implements NavigationPresenter.MyView, IsWidget {

    private final Widget widget;

    public interface Binder extends UiBinder<Widget, NavigationView> {
    }

    @UiField
    Button newAction;
    @UiField
    VerticalPanel contextLinks;

    @Inject
    public NavigationView(final Binder binder) {
        widget = binder.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void showContexts(List<ContextProxy> contexts) {
        for (ContextProxy context : contexts) {
            contextLinks.add(new InlineHyperlink(context.getName(), "!contexts"));
        }
    }

    @UiHandler("newAction")
    void onNewActionButtonClicked(ClickEvent event) {
        if (getUiHandlers() != null) {
            getUiHandlers().onNewAction();
        }
    }

}