package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

import java.util.Date;
import java.util.List;

@ProxyForName(value = "org.dodgybits.shuffle.server.model.TaskQuery",
        locator="org.dodgybits.shuffle.server.locator.ObjectifyLocator"  )
public interface TaskQueryProxy extends EntityProxy {

    String getName();
    void setName(String name);

    Flag getActive();
    void setActive(Flag value);

    Flag getDeleted();
    void setDeleted(Flag value);

    List<Long> getProjectIds();
    void setProjectIds(List<Long> projectIds);

    List<Long> getContextIds();
    void setContextIds(List<Long> contextIds);

    Date getDueDateFrom();
    void setDueDateFrom(Date dueDateFrom);

    Date getDueDateTo();
    void setDueDateTo(Date dueDateTo);

    PredefinedQuery getPredefinedQuery();
    void setPredefinedQuery(PredefinedQuery query);

    EntityProxyId<TaskQueryProxy> stableId();

}
