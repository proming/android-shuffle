package org.dodgybits.shuffle.server.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.*;
import org.dodgybits.shuffle.shared.Flag;
import org.dodgybits.shuffle.shared.PredefinedQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskService {
    private static final Logger log = Logger.getLogger(TaskService.class.getName());

    private ObjectifyDao<WatchedTask> mTaskDao = ObjectifyDao.newDao(WatchedTask.class);

    public TaskQueryResult query(TaskQuery query, int start, int limit) {
        log.log(Level.FINEST, "Looking up using {0} start {1} limit {2}",
                new Object[] {query, start, limit});

        TaskQueryResult result = new TaskQueryResult();

        Query<WatchedTask> q = mTaskDao.userQuery();

        applyPredefinedQuery(query.getPredefinedQuery(), q);
        applyFlag(query.getActive(), "active", q);
        applyFlag(query.getDeleted(), "deleted", q);

        if (query.getDueDateFrom() != null) {
            q.filter("dueDate >=", query.getDueDateFrom());
        }
        
        if (query.getDueDateTo() != null) {
            q.filter("dueDate <=", query.getDueDateTo());
        }

        result.setOffset(start);
        result.setTotalCount(q.count());

        q.limit(limit).offset(start);
        result.setEntities(q.list());

        return result;
    }

    public WatchedTask save(WatchedTask task)
    {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        task.setOwner(loggedInUser);

        Key<WatchedProject> previousSequentialProjectKey = null;
        Key<WatchedProject> currentSequentialProjectKey = null;
        
        if (task.isProjectChanged()) {
            if (task.getPreviousSequentialProjectKey() != null && task.isTopTask()) {
                // just moved from a sequential project where this was the top task
                // need to find a new top task
                previousSequentialProjectKey = task.getPreviousSequentialProjectKey();
            }
        }

        // active and deleted flags are now all up to date
        // topTask needs to be recalculated
        if (task.getProjectKey() == null) {
            task.setTopTask(isCandidateTopTask(task));
        } else {
            if (task.isParallelProject()) {
                task.setTopTask(isCandidateTopTask(task));
            } else {
                if (task.isTopTask() != isCandidateTopTask(task)) {
                    // either this wasn't a top task and could now be one,
                    // or this was a top task and no longer can be one
                    // either way we need to pick a new top task for this project
                    currentSequentialProjectKey = task.getProjectKey();
                }
            }
        }

        mTaskDao.put(task);

        if (previousSequentialProjectKey != null) {
            updateTopTaskForSequentialProject(previousSequentialProjectKey);
        }
        if (currentSequentialProjectKey != null) {
            updateTopTaskForSequentialProject(currentSequentialProjectKey);
        }

        if (task.getProjectKey() != null && (task.getId() == null || task.isProjectChanged())) {
            // either this is a brand new task, or has just moved into a project
            // in either case, move it to the correct order in the new project
            addTaskToProject(task.getProjectKey(), task);
        }        

        return task;
    }

    /**
     * Reposition a task within a project.
     * 
     * @param movedTask task that is being moved
     * @param desiredOrder where the task should go.
     */
    public void moveBelow(WatchedTask movedTask, int desiredOrder) {
        int currentOrder = movedTask.getOrder();
        boolean moveUp = desiredOrder < currentOrder;

        boolean mayBecomeTopTask = false;
        boolean mayLoseTopTask = false;
        
        if (!movedTask.isParallelProject()) {
            if (moveUp) {
                if (isCandidateTopTask(movedTask) && !movedTask.isTopTask()) {
                    // check if this becomes the top task after the move
                    mayBecomeTopTask = true;
                }
            } else {
                if (movedTask.isTopTask()) {
                    // check if this is no longer the top task after the move
                    mayLoseTopTask = true;
                }
            }
        }
        
        List<WatchedTask> tasks = mTaskDao.userQuery().
                filter("project", movedTask.getProjectKey()).
                filter("order >=", moveUp ? desiredOrder : currentOrder + 1).
                filter("order <=", moveUp ? currentOrder - 1 : desiredOrder).
                order("order").list();
        movedTask.setOrder(desiredOrder);
        for (WatchedTask task : tasks) {
            int order = task.getOrder();
            if (moveUp) {
                task.setOrder(order + 1);
                if (mayBecomeTopTask && task.isTopTask()) {
                    task.setTopTask(false);
                    movedTask.setTopTask(true);
                    mayBecomeTopTask = false;
                }
            } else {
                task.setOrder(order - 1);
                if (mayLoseTopTask && isCandidateTopTask(task)) {
                    task.setTopTask(true);
                    movedTask.setTopTask(false);
                    mayLoseTopTask = false;
                }
            }
        }
        mTaskDao.put(movedTask);
        mTaskDao.putAll(tasks);
    }
    

    public void delete(WatchedTask task)
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

    public int deleteCompletedTasks() {
        List<WatchedTask> tasks = mTaskDao.userQuery().filter("completed", true).list();
        log.log(Level.INFO, "Deleting {0} completed tasks", tasks.size());

        for (WatchedTask task : tasks) {
            task.setDeletedTask(true);
        }
        mTaskDao.putAll(tasks);
        
        return tasks.size();
    }

    /**
     * A flag on the project has changed that has an impact on task related queries.
     */
    public void onProjectChanged(WatchedProject project, Key<WatchedProject> key) {
        List<WatchedTask> tasks = mTaskDao.userQuery().filter("project", key).order("order").list();
        boolean sequentialTopTaskFound = false;
        for (WatchedTask task : tasks) {
            if (project.isActiveChanged()) {
                task.setActiveProject(project.isActive());
            }
            if (project.isDeletedChanged()) {
                task.setDeletedProject(project.isDeleted());
            }
            if (project.isParallelChanged()) {
                task.setParallelProject(project.isParallel());
            }
            
            boolean isCandidate = isCandidateTopTask(task);
            task.setTopTask(isCandidate && (project.isParallel() || !sequentialTopTaskFound));
            if (!project.isParallel() && task.isTopTask()) {
                sequentialTopTaskFound = true;
            }
        }
        mTaskDao.putAll(tasks);
    }

    /**
     * A flag on the context has changed that has an impact on task related queries.
     */
    public void onContextChanged(WatchedContext context, Key<WatchedContext> key) {
        List<WatchedTask> tasks = mTaskDao.userQuery().filter("contexts", key).list();
        Set<Key<WatchedProject>> projectsToUpdate = Sets.newHashSet();
        
        for (WatchedTask task : tasks) {
            boolean wasCandidate = isCandidateTopTask(task);
            if (context.isActiveChanged()) {
                if (context.isActive()) {
                    task.incrementActiveContextCount();
                } else {
                    task.decrementActiveContextCount();
                }
            }
            if (context.isDeletedChanged()) {
                if (context.isDeleted()) {
                    task.incrementDeletedContextCount();
                } else {
                    task.decrementDeletedContextCount();
                }
            }

            boolean isCandidate = isCandidateTopTask(task);
            if (task.isParallelProject()) {
                task.setTopTask(isCandidate);
            } else {
                if (task.isTopTask() && !isCandidate) {
                    // this was the project top task, but no longer - need to find a new top task for this project
                    projectsToUpdate.add(task.getProjectKey());
                } else if (!task.isTopTask() && !wasCandidate && isCandidate) {
                    // this wasn't a top task or a candidate, but now it is - check if it is the new top task for this project
                    projectsToUpdate.add(task.getProjectKey());
                }
            }
        }
        mTaskDao.putAll(tasks);

        for (Key<WatchedProject> projectKey : projectsToUpdate) {
            updateTopTaskForSequentialProject(projectKey);
        }
    }

    /**
     * @return true if the task is active and not deleted
     */
    private boolean isCandidateTopTask(WatchedTask task) {
        return task.isActive() && !task.isDeleted();
    }

    private void applyPredefinedQuery(PredefinedQuery predefinedQuery, Query<WatchedTask> q) {
        switch (predefinedQuery) {
            case inbox:
                q.filter("inboxTask", true);
                break;
            case nextTasks:
                q.filter("topTask", true);
                break;
        }
    }

    private void applyFlag(Flag flag, String field, Query<WatchedTask> q) {
        switch (flag) {
            case yes:
                q.filter(field, true);
                break;
            case no:
                q.filter(field, false);
        }
    }


    /**
     * Insert the task into the project with the appropriate order, shuffling later
     * tasks down as needed.
     * If the task has a due date, insert it before the first task
     * it finds with a later due date. Otherwise add to the end of the list.
     * All task order attributes should be sequential starting at 0.
     *
     * @param projectKey project to update
     * @param newTask either a brand new task or one that has just moved into this project
     */
    private void addTaskToProject(Key<WatchedProject> projectKey, WatchedTask newTask) {
        List<WatchedTask> tasks = mTaskDao.userQuery().filter("project", projectKey).order("order").list();
        int order = 0;
        ArrayList<WatchedTask> changedTasks = Lists.newArrayList();
        boolean hasDueDate = newTask.getDueDate() != null;
        boolean placedNewTask = false;
        
        for (WatchedTask task : tasks) {
            if (task.getId().equals(newTask.getId())) {
                // ignore new task in list for now
                continue;
            }
            
            if (hasDueDate && !placedNewTask) {
                if (task.getDueDate() != null &&
                        task.getDueDate().after(newTask.getDueDate())) {
                    if (newTask.getOrder() != order) {
                        newTask.setOrder(order++);
                        changedTasks.add(newTask);
                    }
                    placedNewTask = true;
                }
            }
            
            if (task.getOrder() != order) {
                task.setOrder(order++);
                changedTasks.add(task);
            }
        }
        if (!placedNewTask && newTask.getOrder() != order) {
            newTask.setOrder(order);
            changedTasks.add(newTask);
        }
        
        if (!changedTasks.isEmpty()) {
            mTaskDao.putAll(changedTasks);
        }
    }

    /**
     * Reassign a top task for the given sequential project.
     * 
     * @param projectKey sequential project to find the top task for
     */
    private void updateTopTaskForSequentialProject(Key<WatchedProject> projectKey) {
        List<WatchedTask> tasks = mTaskDao.userQuery().filter("project", projectKey).order("order").list();
        boolean topTaskFound = false;
        ArrayList<WatchedTask> changedTasks = Lists.newArrayList();

        for (WatchedTask task : tasks) {
            if (!topTaskFound && isCandidateTopTask(task)) {
                log.log(Level.FINE, "Found new top task {0} for project {1}", 
                        new Object[] {task, projectKey});
                topTaskFound = true;
                if (!task.isTopTask()) {
                    task.setTopTask(true);
                    changedTasks.add(task);
                }
            } else {
                if (task.isTopTask()) {
                    task.setTopTask(false);
                    changedTasks.add(task);
                }
            }
        }
        if (!changedTasks.isEmpty()) {            
            mTaskDao.putAll(changedTasks);
        }
    }


    
}
