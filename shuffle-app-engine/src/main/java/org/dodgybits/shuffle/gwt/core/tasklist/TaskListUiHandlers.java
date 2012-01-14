package org.dodgybits.shuffle.gwt.core.tasklist;

import com.google.gwt.view.client.HasData;
import com.gwtplatform.mvp.client.UiHandlers;
import org.dodgybits.shuffle.shared.TaskProxy;

public interface TaskListUiHandlers extends UiHandlers {

    void onEditAction(int index, TaskProxy proxy);
    void setDisplay(HasData<TaskProxy> view);
}
