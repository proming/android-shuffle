package org.dodgybits.shuffle.server.service;

import java.util.List;

import com.google.appengine.api.datastore.EntityNotFoundException;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.Task;


public class TaskDao extends ObjectifyDao<Task> {
    
    @Override
    public List<Task> listAll()
    {
        List<Task> tasks = listAllForUser();
        return tasks;
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
            AppUser loggedInUser = LoginService.getLoggedInUser();
            if (!task.getOwner().equals(loggedInUser)) {
                // wrong user - bail
                task = null;
            }
        } catch (EntityNotFoundException e) {
            // couldn't find task
        }
        return task;
    }
}
