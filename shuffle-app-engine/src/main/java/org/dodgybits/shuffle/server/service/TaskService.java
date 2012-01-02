package org.dodgybits.shuffle.server.service;

import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.*;
import org.dodgybits.shuffle.shared.Flag;
import org.dodgybits.shuffle.shared.PredefinedQuery;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskService {
    private static final Logger log = Logger.getLogger(TaskService.class.getName());

    private ObjectifyDao<Task> mTaskDao = ObjectifyDao.newDao(Task.class);
    private ObjectifyDao<TaskQuery> mTaskQueryDao = ObjectifyDao.newDao(TaskQuery.class);

    public TaskQueryResult query(TaskQuery query, int start, int limit) {
        log.log(Level.FINEST, "Looking up using {0} start {1} limit {2}",
                new Object[] {query, start, limit});

        TaskQueryResult result = new TaskQueryResult();

        Query<Task> q = mTaskDao.userQuery();

        applyPredefinedQuery(query.getPredefinedQuery(), q);
        applyFlag(query.getActive(), "active", q);
        applyFlag(query.getDeleted(), "deleted", q);

        result.setOffset(start);
        result.setTotalCount(q.count());

        q.limit(limit).offset(start);
        result.setEntities(q.list());

        return result;
    }

    private void applyPredefinedQuery(PredefinedQuery predefinedQuery, Query<Task> q) {
        switch (predefinedQuery) {
            case inbox:
//                result = "(projectId is null AND contextId is null)";
                q.filter("inboxTask", true);
                break;
            case nextTasks:
                q.filter("topTask", true);
                break;
        }
    }

    private void applyFlag(Flag flag, String field, Query<Task> q) {
        switch (flag) {
            case yes:
                q.filter(field, true);
                break;
            case no:
                q.filter(field, false);
        }
        // TODO - apply values from contexts and project too
    }

    public Task save(Task task)
    {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        task.setOwner(loggedInUser);

        if (task.getId() != null) {
            Task oldTask = mTaskDao.get(task.getId());
            boolean activeChanged = oldTask.isActive() != task.isActive();
            boolean deletedChanged = oldTask.isDeleted() != task.isDeleted();
            if (activeChanged || deletedChanged) {
                log.log(Level.FINE, "Active changed {0} deleted changed {1}",
                        new Object[] {activeChanged, deletedChanged});
            }
        }
        
        mTaskDao.put(task);
        return task;
    }

    public void delete(Task task)
    {
        mTaskDao.delete(task);
    }

    public Task findById(Long id)
    {
        Task task = null;
        try {
            task = mTaskDao.get(id);
            log.log(Level.FINE, "Looking up task {0}", id);
            AppUser loggedInUser = LoginService.getLoggedInUser();
            if (!task.getOwner().equals(loggedInUser)) {
                log.log(Level.WARNING, "User {0} for task {1} doesn't match logged in user {2}",
                        new Object[] {task.getOwner(), id, loggedInUser});
                // wrong user - bail
                task = null;
            }
        } catch (NotFoundException e) {
            log.log(Level.WARNING, "Task {0} not found", id);
        }
        return task;
    }

    public void emptyTrash() {
        log.log(Level.FINE, "Emptying trash");
    }

    public Integer deleteCompletedTasks() {
        log.log(Level.FINE, "Deleting completed tasks");
        return 0;
    }

    public TaskQuery save(TaskQuery query) {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        query.setOwner(loggedInUser);
        mTaskQueryDao.put(query);
        return query;
    }

    public TaskQuery findQueryByName(String name) {
        TaskQuery taskQuery = null;
        Query<TaskQuery> query = mTaskQueryDao.userQuery();
        query.filter("name", name);
        log.log(Level.FINE, "Looking up task query {0}", name);
        List<TaskQuery> queries = query.list();
        if (queries.size() == 1) {
            taskQuery = queries.get(0);
        } else if (queries.size() > 1) {
            throw new IllegalStateException("More than query found for name " + name);
        }
        return taskQuery;
    }
    
    public void swapTasks(Task firstTask, Task secondTask) {
        int tmpOrder = secondTask.getOrder();
        secondTask.setOrder(firstTask.getOrder());
        firstTask.setOrder(tmpOrder);

        boolean tmpTopTask = secondTask.isTopTask();
        secondTask.setTopTask(tmpTopTask);
        firstTask.setTopTask(tmpTopTask);

        save(firstTask);
        save(secondTask);
    }

}
