package org.dodgybits.shuffle.gwt.core;

import com.gwtplatform.mvp.client.UiHandlers;
import org.dodgybits.shuffle.shared.TaskProxy;

public interface TaskListUiHandlers extends UiHandlers {

    void onEditAction(TaskProxy proxy);

}
