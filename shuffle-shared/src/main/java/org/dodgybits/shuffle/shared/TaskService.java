package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import java.util.List;

@ServiceName(value = "org.dodgybits.shuffle.server.service.TaskService",
        locator="org.dodgybits.shuffle.server.locator.DaoServiceLocator")
public interface TaskService extends RequestContext {

    Request<TaskProxy> save(TaskProxy newTask);

    Request<Void> delete(TaskProxy task);

    Request<Integer> deleteCompletedTasks();

    Request<TaskQueryResultProxy> query(TaskQueryProxy query, int start, int limit);

    Request<Void> moveBelow(TaskProxy movedTask, int desiredOrder);

}
