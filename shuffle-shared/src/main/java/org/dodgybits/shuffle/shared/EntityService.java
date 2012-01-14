package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import java.util.List;

@ServiceName(value = "org.dodgybits.shuffle.server.service.EntityService",
        locator="org.dodgybits.shuffle.server.locator.DaoServiceLocator")
public interface EntityService extends RequestContext {

    Request<TaskProxy> save(TaskProxy newTask);

    Request<Void> delete(TaskProxy task);

    Request<Integer> deleteCompletedTasks();

    Request<TaskQueryResultProxy> query(String queryName, int start, int limit);

    Request<TaskQueryResultProxy> query(TaskQueryProxy query, int start, int limit);

    Request<Void> moveBelow(TaskProxy movedTask, int desiredOrder);

    Request<ContextProxy> save(ContextProxy newContext);

    Request<TaskQueryProxy> save(TaskQueryProxy query);

    Request<TaskQueryProxy> findQueryByName(String name);
    
    Request<Void> delete(ContextProxy context);

    Request<List<ContextProxy>> fetchAllContexts();

    Request<ProjectProxy> save(ProjectProxy newProject);

    Request<Void> delete(ProjectProxy project);

    Request<List<ProjectProxy>> fetchAllProjects();


}
