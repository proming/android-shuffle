package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName(value = "org.dodgybits.shuffle.server.service.TaskQueryService",
        locator="org.dodgybits.shuffle.server.locator.DaoServiceLocator")
public interface TaskQueryService  extends RequestContext {

    Request<TaskQueryProxy> save(TaskQueryProxy query);

    Request<TaskQueryProxy> findQueryByName(String name);


}
