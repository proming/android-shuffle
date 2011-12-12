package org.dodgybits.shuffle.server.service;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.Task;
import org.dodgybits.shuffle.server.model.TaskQuery;
import org.dodgybits.shuffle.server.model.TaskQueryResult;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskService {
    private static final Logger log = Logger.getLogger(TaskService.class.getName());

    private TaskDao mDao = new TaskDao();

    public TaskQueryResult query(TaskQuery query, int start, int limit) {
        log.log(Level.FINEST, "Looking up using {0}", query);

        TaskQueryResult result = new TaskQueryResult();

        Query<Task> q = mDao.userQuery();
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
        mDao.put(task);
        return task;
    }

    public void delete(Task task)
    {
        mDao.delete(task);
    }

    public Task findById(Long id)
    {
        Task task = null;
        try {
            task = mDao.get(id);
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
}
