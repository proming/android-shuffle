package org.dodgybits.shuffle.gwt.core;

import com.google.gwt.view.client.ListDataProvider;
import com.gwtplatform.mvp.client.UiHandlers;
import org.dodgybits.shuffle.shared.TaskProxy;

public interface TaskListUiHandlers extends UiHandlers {

    void onEditAction(TaskProxy proxy);
    ListDataProvider<TaskProxy> getDataProvider();
}
