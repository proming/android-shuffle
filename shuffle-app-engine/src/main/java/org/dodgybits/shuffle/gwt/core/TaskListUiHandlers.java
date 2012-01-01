package org.dodgybits.shuffle.gwt.core;

import com.google.gwt.view.client.HasData;
import com.gwtplatform.mvp.client.UiHandlers;
import org.dodgybits.shuffle.shared.ContextProxy;
import org.dodgybits.shuffle.shared.ProjectProxy;
import org.dodgybits.shuffle.shared.TaskProxy;

import java.util.List;

public interface TaskListUiHandlers extends UiHandlers {

    void onEditAction(int index, TaskProxy proxy);
    void setDisplay(HasData<TaskProxy> view);
    ProjectProxy getProject(TaskProxy task);
    List<ContextProxy> getContexts(TaskProxy task);
}
