package org.dodgybits.shuffle.server.service;

import com.google.appengine.api.datastore.EntityNotFoundException;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.Task;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TaskDao extends ObjectifyDao<Task> {
    private static final Logger log = Logger.getLogger(TaskDao.class.getName());

    @Override
    public List<Task> listAll()
    {
        List<Task> tasks = listAllForUser();
        return tasks;
    }

    public List<Task> listRange(int offset, int limit)
    {
        return listRangeForUser(offset, limit);
    }

    /**
     * Wraps put() so as not to return a Key, which RequestFactory can't handle
     */
    public void save(Task task)
    {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        task.setOwner(loggedInUser);
        this.put(task);
    }

    public Task saveAndReturn(Task task)
    {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        task.setOwner(loggedInUser);
        this.put(task);
        return task;
    }
    
    public void deleteTask(Task task)
    {
        this.delete(task);
    }

    public Task findById(Long id)
    {
        Task task = null;
        try {
            task = super.get(id);
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
}
