package org.dodgybits.shuffle.server.service;

import com.google.common.collect.Lists;
import org.dodgybits.shuffle.server.model.TaskQuery;
import org.dodgybits.shuffle.server.model.WatchedContext;
import org.dodgybits.shuffle.server.model.WatchedProject;
import org.dodgybits.shuffle.server.model.WatchedTask;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalService {
    private static final Logger log = Logger.getLogger(GlobalService.class.getName());

    private ObjectifyDao<WatchedTask> mTaskDao = ObjectifyDao.newDao(WatchedTask.class);
    private ObjectifyDao<WatchedContext> mContextDao = ObjectifyDao.newDao(WatchedContext.class);
    private ObjectifyDao<WatchedProject> mProjectDao = ObjectifyDao.newDao(WatchedProject.class);

    /**
     * Permanently delete all custom contexts, projects and tasks.
     *
     * @return count of tasks, projects and contexts deleted.
     */
    public List<Integer> deleteEverything() {
        log.log(Level.FINE, "Delete everything");

        List<Integer> deletedCounts = Lists.newArrayList();

        List<WatchedTask> tasks = mTaskDao.userQuery().list();
        deletedCounts.add(tasks.size());
        log.log(Level.INFO, "Permanently deleting {0} tasks", tasks.size());
        mTaskDao.deleteAll(tasks);

        List<WatchedProject> projects = mProjectDao.userQuery().list();
        deletedCounts.add(projects.size());
        log.log(Level.INFO, "Permanently deleting {0} projects", projects.size());
        mProjectDao.deleteAll(projects);

        List<WatchedContext> contexts = mContextDao.userQuery().list();
        deletedCounts.add(contexts.size());
        log.log(Level.INFO, "Permanently deleting {0} contexts", contexts.size());
        mContextDao.deleteAll(contexts);

        return deletedCounts;
    }

    /**
     * Permanently delete all contexts, projects and tasks that are marked as deleted.
     *
     * @return count of tasks, projects and contexts deleted.
     */
    public List<Integer> emptyTrash() {
        log.log(Level.FINE, "Emptying trash");

        List<Integer> deletedCounts = Lists.newArrayList();

        List<WatchedTask> tasks = mTaskDao.userQuery().filter("deleted", true).list();
        deletedCounts.add(tasks.size());
        log.log(Level.INFO, "Permanently deleting {0} tasks", tasks.size());
        mTaskDao.deleteAll(tasks);

        List<WatchedProject> projects = mProjectDao.userQuery().filter("deleted", true).list();
        deletedCounts.add(projects.size());
        log.log(Level.INFO, "Permanently deleting {0} projects", projects.size());
        mProjectDao.deleteAll(projects);

        List<WatchedContext> contexts = mContextDao.userQuery().filter("deleted", true).list();
        deletedCounts.add(contexts.size());
        log.log(Level.INFO, "Permanently deleting {0} contexts", contexts.size());
        mContextDao.deleteAll(contexts);

        return deletedCounts;
    }

}
