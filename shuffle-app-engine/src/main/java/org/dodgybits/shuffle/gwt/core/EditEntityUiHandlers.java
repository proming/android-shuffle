package org.dodgybits.shuffle.gwt.core;

import com.gwtplatform.mvp.client.UiHandlers;
import org.dodgybits.shuffle.shared.TaskProxy;

public interface EditEntityUiHandlers extends UiHandlers {
    void save(String description, String details);
    void cancel();
    void previous();
    void next();
}
