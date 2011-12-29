package org.dodgybits.shuffle.server.service;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.Task;
import org.dodgybits.shuffle.server.model.TaskQuery;
import org.dodgybits.shuffle.server.model.TaskQueryResult;

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
        result.setTotalCount(q.count());
        q.limit(limit).offset(start);
        result.setEntities(q.list());
        result.setOffset(start);

        return result;
    }

    public Task save(Task task)
    {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        task.setOwner(loggedInUser);
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
        } catch (EntityNotFoundException e) {
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
    
}
