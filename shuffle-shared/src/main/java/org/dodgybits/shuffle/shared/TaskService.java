package org.dodgybits.shuffle.shared;

import java.util.*;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName(value = "org.dodgybits.shuffle.server.service.TaskService", locator="org.dodgybits.shuffle.server.locator.DaoServiceLocator")
public interface TaskService extends RequestContext {

    Request<TaskProxy> save(TaskProxy newTask);

    Request<Void> delete(TaskProxy task);

    Request<TaskProxy> findById(Long id);

    Request<Void> emptyTrash();

    Request<Integer> deleteCompletedTasks();

    Request<TaskQueryResultProxy> query(TaskQueryProxy query);

}
