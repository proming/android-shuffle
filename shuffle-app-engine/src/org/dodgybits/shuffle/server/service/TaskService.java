package org.dodgybits.shuffle.server.service;

import java.util.List;

import org.dodgybits.shuffle.server.model.Task;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;


public class TaskService {
    
    static {
        ObjectifyService.register(Task.class);
    }
    
    public static Task createTask() {
        Objectify ofy = ObjectifyService.begin();
        Task task = new Task();
        ofy.put(task);
        return task;
    }

    public static Task readTask(Long id) {
        Objectify ofy = ObjectifyService.begin();
        return ofy.get(Task.class, id);
    }

    public static Task updateTask(Task task) {
        Objectify ofy = ObjectifyService.begin();
        ofy.put(task);
        return task;
    }

    public static void deleteTask(Task task) {
        Objectify ofy = ObjectifyService.begin();
        ofy.delete(task);
    }

    public static List<Task> queryTasks() {
        Objectify ofy = ObjectifyService.begin();
        Query<Task> query = ofy.query(Task.class);
        return query.list();
    }
}
