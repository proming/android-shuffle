package org.dodgybits.shuffle.gwt.core;

import com.gwtplatform.mvp.client.UiHandlers;
import org.dodgybits.shuffle.shared.ContextProxy;
import org.dodgybits.shuffle.shared.ProjectProxy;
import org.dodgybits.shuffle.shared.TaskProxy;

import java.util.List;

public interface EditEntityUiHandlers extends UiHandlers {
    void save(String description, String details);
    void cancel();
    void previous();
    void next();
    ProjectProxy getProject(TaskProxy task);
    List<ContextProxy> getContexts(TaskProxy task);

}
