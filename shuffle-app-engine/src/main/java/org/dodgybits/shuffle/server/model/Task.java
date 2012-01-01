package org.dodgybits.shuffle.server.model;

import com.google.appengine.api.datastore.Text;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import org.dodgybits.shuffle.server.service.ObjectifyDao;

import javax.annotation.Nullable;
import javax.persistence.PrePersist;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Entity
@Unindexed
public class Task extends UserDatastoreObject {
    private static final Logger log = Logger.getLogger(Task.class.getName());

    private String description;
    private Text details;
    private List<Key<Context>> contexts = Lists.newArrayList();
    private Key<Project> project;
    private Date createdDate;
    @Indexed
    private Date modifiedDate;
    private Date showFromDate;
    private Date dueDate;
    private boolean mAllDay;
    // 0-indexed order within a project.
    @Indexed()
    private int order;
    @Indexed
    private boolean complete;

    @Indexed
    private boolean topTask;

    private boolean deletedTask;
    private boolean deletedProject = false;
    private int deletedContextCount = 0;
    @Indexed
    private boolean deleted;

    private boolean activeTask = true;
    private boolean activeProject = true;
    private int activeContextCount = 0;
    @Indexed
    private boolean active = true;

    public List<Key<Context>> getContextKeys() {
        return contexts;
    }

    public void setContextKeys(List<Key<Context>> contexts) {
        this.contexts = contexts;
    }
    
    public List<Long> getContextIds() {
        return Lists.transform(contexts, new Function<Key<Context>, Long>() {
            @Override
            public Long apply(@Nullable Key<Context> contextKey) {
                Long id = null;
                if (contextKey != null) {
                    id = contextKey.getId();
                }
                return id;
            }
        });
    }
    
    public void setContextIds(List<Long> ids) {
        contexts = Lists.newArrayList(Lists.transform(ids, new Function<Long, Key<Context>>() {
            @Override
            public Key<Context> apply(@Nullable Long input) {
                return new Key<Context>(Context.class, input);
            }
        }));
    }

    public Key<Project> getProjectKey() {
        return project;
    }

    public void setProjectKey(Key<Project> project) {
        this.project = project;
        updateOrderAndTopTask();
    }
    
    private void updateOrderAndTopTask() {
        // TODO take into account deleted, active and completed flags

        if (project == null) {
            log.log(Level.FINER, "Task {0} is topTask since it has no project", getId());
            topTask = true;
        } else {
            Project project = getProject();
            ObjectifyDao<Task> taskDao = ObjectifyDao.newDao(Task.class);
            // if adding to a project, add as last task
            Query<Task> q = taskDao.userQuery().filter("project", this.project).order("-order").limit(1);
            Task task = q.get();
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

    public boolean isTopTask() {
        return topTask;
    }

    public void setTopTask(boolean value) {
        topTask = value;
    }

    public Long getProjectId() {
        Long id = null;
        if (project != null) {
            id = project.getId();
        }
        return id;
    }
    
    public void setProjectId(Long id) {
        if (id == null) {
            setProjectKey(null);
        } else {
            setProjectKey(new Key<Project>(Project.class, id));
        }
    }
    
    private Project getProject() {
        Project project = null;
        if (this.project != null) {
            ObjectifyDao<Project> projectDao = ObjectifyDao.newDao(Project.class);
            project = projectDao.get(this.project);
        }
        return project;
    }

    public Date getShowFromDate() {
        return showFromDate;
    }

    public void setShowFromDate(Date showFromDate) {
        this.showFromDate = showFromDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isAllDay() {
        return mAllDay;
    }

    public void setAllDay(boolean allDay) {
        mAllDay = allDay;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final String getDetails() {
        return details.getValue();
    }

    public final void setDetails(String details) {
        this.details = new Text(details);
    }

    public final Date getCreatedDate() {
        return createdDate;
    }

    public final void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public final Date getModifiedDate() {
        return modifiedDate;
    }

    public final boolean isActive() {
        return activeTask;
    }

    public final void setActive(boolean active) {
        this.activeTask = active;
        updateActive();
    }

    public final void setActiveProject(boolean active) {
        this.activeProject = active;
        updateActive();
    }

    public final void decrementActiveContextCount() {
        activeContextCount--;
        updateActive();
    }

    public final void incrementActiveContextCount() {
        activeContextCount++;
        updateActive();
    }

    private void updateActive() {
        active = (activeTask && activeProject &&
                (contexts.size() == 0 || activeContextCount > 0));
    }

    public final boolean isDeleted() {
        return deletedTask;
    }

    public final void setDeleted(boolean deleted) {
        this.deletedTask = deleted;
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
        deleted = (deletedTask || deletedProject ||
                (contexts.size() > 0 && deletedContextCount == contexts.size()));
    }

    public final int getOrder() {
        return order;
    }

    public final void setOrder(int order) {
        this.order = order;
    }

    public final boolean isComplete() {
        return complete;
    }

    public final void setComplete(boolean complete) {
        this.complete = complete;
    }

    @PrePersist
    private void PrePersist() {
        modifiedDate = new Date();
    }

}
