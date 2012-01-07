package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import java.util.Date;

@ProxyForName(value = "org.dodgybits.shuffle.server.model.WatchedContext",
        locator="org.dodgybits.shuffle.server.locator.ObjectifyLocator"  )
public interface ContextProxy extends EntityProxy {

    Long getId();
    String getName();
    void setName(String name);
    int getColourIndex();
    void setColourIndex(int colourIndex);
    String getIconName();
    void setIconName(String iconName);
    Date getModifiedDate();
    boolean isActive();
    void setActive(boolean active);
    boolean isDeleted();
    void setDeleted(boolean deleted);

}
