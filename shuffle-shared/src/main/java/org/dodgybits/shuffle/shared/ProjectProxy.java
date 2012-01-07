package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import java.util.Date;

@ProxyForName(value = "org.dodgybits.shuffle.server.model.WatchedProject",
        locator="org.dodgybits.shuffle.server.locator.ObjectifyLocator"  )
public interface ProjectProxy extends EntityProxy {

    Long getId();
    String getName();
    void setName(String name);
    Long getDefaultContextId();
    void setDefaultContextId(Long defaultContextId);
    boolean isParallel();
    void setParallel(boolean parallel);
    boolean isArchived();
    void setArchived(boolean archived);
    Date getModifiedDate();
    boolean isActive();
    void setActive(boolean active);
    boolean isDeleted();
    void setDeleted(boolean deleted);

}
