package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyForName;

import java.util.Date;
import java.util.List;

@ProxyForName(value = "org.dodgybits.shuffle.server.model.WatchedTask",
        locator="org.dodgybits.shuffle.server.locator.ObjectifyLocator"  )
public interface TaskProxy extends EntityProxy {

    Long getId();
    List<Long> getContextIds();
    void setContextIds(List<Long> contextIds);
    Long getProjectId();
    void setProjectId(Long projectId);
    Date getShowFromDate();
    void setShowFromDate(Date showFromDate);
    Date getDueDate();
    void setDueDate(Date dueDate);
    boolean isAllDay();
    void setAllDay(boolean allDay);
    String getDescription();
    void setDescription(String description);
    String getDetails();
    void setDetails(String details);
    Date getCreatedDate();
    void setCreatedDate(Date createdDate);
    Date getModifiedDate();
    boolean isActive();
    void setActive(boolean active);
    boolean isDeleted();
    void setDeleted(boolean deleted);
    int getOrder();
    void setOrder(int order);
    boolean isComplete();
    void setComplete(boolean complete);

}
