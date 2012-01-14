package org.dodgybits.shuffle.gwt.core;

import com.gwtplatform.mvp.client.UiHandlers;

public interface EditEntityUiHandlers extends UiHandlers {
    void save(String description, String details);
    void cancel();
    void previous();
    void next();
}
