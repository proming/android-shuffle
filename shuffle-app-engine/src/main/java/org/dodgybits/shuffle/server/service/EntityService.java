package org.dodgybits.shuffle.server.service;

import org.dodgybits.shuffle.server.model.*;

import java.util.List;

/**
 * Combine task, context and project services into one
 * client facing interface. This makes it possible to chain
 * multiple requests in a single call using RequestFactory.
 */
public class EntityService {
    private TaskService mTaskService = new TaskService();
    private TaskQueryService mTaskQueryService = new TaskQueryService();
    private ProjectService mProjectService = new ProjectService();
    private ContextService mContextService = new ContextService();

    public TaskQueryResult query(TaskQuery query, int start, int limit) {
        return mTaskService.query(query, start, limit);
    }

    public TaskQueryResult query(String queryName, int start, int limit) {
        TaskQuery query = mTaskQueryService.findQueryByName(queryName);
        return mTaskService.query(query, start, limit);
    }

    public WatchedTask save(WatchedTask task) {
        return mTaskService.save(task);
    }

    public void delete(WatchedTask task)
    {
        mTaskService.delete(task);
    }

    public void moveBelow(WatchedTask movedTask, int desiredOrder) {
        mTaskService.moveBelow(movedTask, desiredOrder);
    }

    public int deleteCompletedTasks() {
        return mTaskService.deleteCompletedTasks();
    }

    public TaskQuery save(TaskQuery query) {
        return mTaskQueryService.save(query);
    }

    public TaskQuery findQueryByName(String name) {
        return mTaskQueryService.findQueryByName(name);
    }

    public List<WatchedContext> fetchAllContexts() {
        return mContextService.fetchAll();
    }

    public WatchedContext save(WatchedContext context)
    {
        return mContextService.save(context);
    }

    public void delete(WatchedContext context) {
        mContextService.delete(context);
    }

    public List<WatchedProject> fetchAllProjects() {
        return mProjectService.fetchAll();
    }

    public WatchedProject save(WatchedProject project)
    {
        return mProjectService.save(project);
    }

    public void delete(WatchedProject project) {
        mProjectService.delete(project);
    }

}
