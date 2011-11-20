package org.dodgybits.shuffle.shared;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName(value = "org.dodgybits.shuffle.server.service.TaskDao", locator="org.dodgybits.shuffle.server.locator.DaoServiceLocator")
public interface TaskService extends RequestContext {
    Request<List<TaskProxy>> listAll();

    Request<Void> save(TaskProxy newTask);

    Request<TaskProxy> saveAndReturn(TaskProxy newTask);

    Request<Void> deleteTask(TaskProxy task);

    Request<TaskProxy> findById(Long id);

}
