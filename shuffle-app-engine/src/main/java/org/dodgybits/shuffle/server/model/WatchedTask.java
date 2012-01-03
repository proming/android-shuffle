package org.dodgybits.shuffle.server.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.condition.IfDefault;
import org.dodgybits.shuffle.server.service.ObjectifyDao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WatchedTask extends Task {
    private static final Logger log = Logger.getLogger(WatchedTask.class.getName());

    @Indexed
    private boolean inboxTask = true;

    @Indexed
    private boolean topTask = false;

    @NotSaved(IfDefault.class)
    private boolean deletedProject = false;

    @NotSaved(IfDefault.class)
    private int deletedContextCount = 0;

    @Indexed
    private boolean deleted;

    @NotSaved(IfDefault.class)
    private boolean activeProject = true;

    @NotSaved(IfDefault.class)
    private int activeContextCount = 0;

    @Indexed
    private boolean active = true;

    @Override
    public void setContextKeys(List<Key<Context>> contexts) {
        super.setContextKeys(contexts);
        updateInbox();
    }

    @Override
    public void setProjectKey(Key<Project> project) {
        super.setProjectKey(project);
        updateOrderAndTopTask();
        updateInbox();
    }

    private void updateInbox() {
        inboxTask = getProjectKey() == null && getContextKeys().isEmpty();
    }

    private void updateOrderAndTopTask() {
        // TODO take into account deleted, active and completed flags

        if (getProjectKey() == null) {
            log.log(Level.FINER, "Task {0} is topTask since it has no project", getId());
            topTask = true;
        } else {
            Project project = getProject();
            ObjectifyDao<WatchedTask> taskDao = ObjectifyDao.newDao(WatchedTask.class);
            // if adding to a project, add as last task
            Query<WatchedTask> q = taskDao.userQuery().filter("project", getProjectKey()).order("-order").limit(1);
            WatchedTask task = q.get();
            if (task == null) {
                log.log(Level.FINER, "Task {0} is topTask since it is the only task in this project", getId());
                topTask = true;
                order = 0;
            } else {
                order = task.order + 1;
                if (project.isParallel()) {
                    log.log(Level.FINER, "Task {0} is topTask since it is in a parallel project", getId());
                    topTask = true;
                } else {
                    log.log(Level.FINER, "Task {0} is not a topTask since it is not the first task in the project", getId());
                    topTask = false;
                }
            }
        }
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        updateActive();
    }

    public void setActiveProject(boolean active) {
        this.activeProject = active;
        updateActive();
    }

    public void decrementActiveContextCount() {
        activeContextCount--;
        updateActive();
    }

    public void incrementActiveContextCount() {
        activeContextCount++;
        updateActive();
    }

    private void updateActive() {
        active = (isActive() && activeProject &&
                (getContextKeys().size() == 0 || activeContextCount > 0));
    }

    public final void setDeletedProject(boolean deleted) {
        this.deletedProject = deleted;
        updateDeleted();
    }

    public final void decrementDeletedContextCount() {
        deletedContextCount--;
        updateDeleted();
    }

    public final void incrementDeletedContextCount() {
        deletedContextCount++;
        updateDeleted();
    }

    private void updateDeleted() {
        deleted = (isDeleted() || deletedProject ||
                (getContextKeys().size() > 0 && deletedContextCount == getContextKeys().size()));
    }

}
