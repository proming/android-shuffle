package org.dodgybits.shuffle.server.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.condition.IfDefault;

import javax.persistence.Transient;
import java.util.List;
import java.util.logging.Logger;

public class WatchedTask extends Task {
    private static final Logger log = Logger.getLogger(WatchedTask.class.getName());

    @Indexed
    private boolean inboxTask = true;

    @Indexed
    private boolean topTask = true;

    @NotSaved(IfDefault.class)
    private int activeContextCount = 0;

    @NotSaved(IfDefault.class)
    private int deletedContextCount = 0;

    @Transient
    private boolean contextsChanged = false;

    @NotSaved(IfDefault.class)
    private boolean activeProject = true;

    @NotSaved(IfDefault.class)
    private boolean deletedProject = false;

    @NotSaved(IfDefault.class)
    private boolean parallelProject = false;

    @Transient
    private Key<WatchedProject> previousSequentialProjectKey = null;
    
    @Transient
    private boolean projectChanged = false;

    @Indexed
    private boolean deleted = false;

    @Indexed
    private boolean active = true;

    @Override
    public void setContextKeys(List<Key<WatchedContext>> contexts) {
        if (!contexts.equals(getContextKeys())) {
            super.setContextKeys(contexts);

            updateContextFlags();
            updateInbox();
            contextsChanged = true;
        }
    }

    @Override
    public void setProjectKey(Key<WatchedProject> project) {
        Key<WatchedProject> previousProjectKey = getProjectKey();
        if (project != previousProjectKey) {
            if (previousProjectKey != null && !parallelProject) {
                this.previousSequentialProjectKey = previousProjectKey;
            }
            super.setProjectKey(project);

            updateProjectFlags();
            updateInbox();
            projectChanged = true;
        }
    }

    public boolean isParallelProject() {
        return parallelProject;
    }

    public void setParallelProject(boolean parallel) {
        parallelProject = parallel;
    }

    public boolean isProjectChanged() {
        return projectChanged;
    }

    public Key<WatchedProject> getPreviousSequentialProjectKey() {
        return previousSequentialProjectKey;
    }

    public boolean isContextsChanged() {
        return contextsChanged;
    }

    public final boolean isActive() {
        return active;
    }

    public final boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setActiveTask(boolean active) {
        super.setActiveTask(active);
        updateActive();
    }

    @Override
    public void setDeletedTask(boolean deleted) {
        super.setDeletedTask(deleted);
        updateDeleted();
    }

    public void setActiveProject(boolean active) {
        this.activeProject = active;
        updateActive();
    }

    public void setTopTask(boolean topTask) {
        this.topTask = topTask;
    }

    public boolean isTopTask() {
        return topTask;
    }

    public void decrementActiveContextCount() {
        activeContextCount--;
        updateActive();
    }

    public void incrementActiveContextCount() {
        activeContextCount++;
        updateActive();
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

    private void updateContextFlags() {
        activeContextCount = deletedContextCount = 0;
        if (!getContextKeys().isEmpty()) {
            List<WatchedContext> contextList = getContexts();
            for (WatchedContext watchedContext : contextList) {
                if (watchedContext.isActive()) {
                    activeContextCount++;
                }
                if (watchedContext.isDeleted()) {
                    deletedContextCount++;
                }
            }
        }
    }

    private void updateProjectFlags() {
        Project newProject = getProject();
        if (newProject == null) {
            parallelProject = false;
            activeProject = true;
            deletedProject = false;
        } else {
            parallelProject = newProject.isParallel();
            activeProject = newProject.isActive();
            deletedProject = newProject.isDeleted();
        }
    }

    private void updateInbox() {
        inboxTask = getProjectKey() == null && getContextKeys().isEmpty();
    }

    private void updateActive() {
        active = (isActiveTask() && activeProject &&
                (getContextKeys().isEmpty() || activeContextCount > 0));
    }

    private void updateDeleted() {
        deleted = (isDeletedTask() || deletedProject ||
                (getContextKeys().size() > 0 && deletedContextCount == getContextKeys().size()));
    }

    @Override
    protected void prePersist() {
        super.prePersist();
        projectChanged = contextsChanged = false;
        previousSequentialProjectKey = null;
    }


}
