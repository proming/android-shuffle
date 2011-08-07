package org.dodgybits.shuffle.shared;

import java.util.Date;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

@ProxyForName(value = "org.dodgybits.shuffle.server.model.Task", locator="org.dodgybits.shuffle.server.service.TaskLocator"  )
public interface TaskProxy extends EntityProxy {
    
    Long getId();
    String getDescription();
    void setDescription(String description);
    String getDetails();
    void setDetails(String details);
    Date getCreatedDate();
    void setCreatedDate(Date createdDate);
    Date getModifiedDate();
    void setModifiedDate(Date modifiedDate);
    boolean isActive();
    void setActive(boolean active);
    boolean isDeleted();
    void setDeleted(boolean deleted);
    int getOrder();
    void setOrder(int order);
    boolean isComplete();
    void setComplete(boolean complete);

}
