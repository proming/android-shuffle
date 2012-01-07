package org.dodgybits.shuffle.server.model;

import com.google.appengine.api.datastore.Text;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfDefault;
import org.dodgybits.shuffle.server.service.ObjectifyDao;

import javax.annotation.Nullable;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Entity
@Unindexed
public class Task extends UserDatastoreObject {
    private static final Logger log = Logger.getLogger(Task.class.getName());

    private String description;
    private Text details;
    
    private List<Key<WatchedContext>> contexts = Lists.newArrayList();
    @Transient
    private List<WatchedContext> watchedContexts = Lists.newArrayList();
    
    private Key<WatchedProject> project;
    @Transient
    private WatchedProject watchedProject;
    
    private Date createdDate;
    @Indexed
    private Date modifiedDate;
    private Date showFromDate;
    private Date dueDate;

    @NotSaved(IfDefault.class)
    private boolean mAllDay = false;
    // 0-indexed order within a project.
    @Indexed
    protected int order = -1;
    @Indexed
    private boolean complete;

    @NotSaved(IfDefault.class)
    private boolean deletedTask = false;

    @NotSaved(IfDefault.class)
    private boolean activeTask = true;

    public List<Key<WatchedContext>> getContextKeys() {
        return contexts;
    }

    public void setContextKeys(List<Key<WatchedContext>> contexts) {
        this.contexts = contexts;
        watchedContexts.clear();
    }
    
    public List<Long> getContextIds() {
        return Lists.transform(contexts, new Function<Key<WatchedContext>, Long>() {
            @Override
            public Long apply(@Nullable Key<WatchedContext> contextKey) {
                Long id = null;
                if (contextKey != null) {
                    id = contextKey.getId();
                }
                return id;
            }
        });
    }
    
    public void setContextIds(List<Long> ids) {
        setContextKeys(Lists.newArrayList(Lists.transform(ids, new Function<Long, Key<WatchedContext>>() {
            @Override
            public Key<WatchedContext> apply(@Nullable Long input) {
                return new Key<WatchedContext>(WatchedContext.class, input);
            }
        })));
    }

    public List<WatchedContext> getContexts() {
        if (watchedContexts.isEmpty() && !contexts.isEmpty()) {
            ObjectifyDao<WatchedContext> contextDao = ObjectifyDao.newDao(WatchedContext.class);
            watchedContexts.addAll(contextDao.get(contexts).values());
        }
        return watchedContexts;
    }
    
    public Key<WatchedProject> getProjectKey() {
        return project;
    }

    public void setProjectKey(Key<WatchedProject> project) {
        this.project = project;
        watchedProject = null;
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
            setProjectKey(new Key<WatchedProject>(WatchedProject.class, id));
        }
    }
    
    public WatchedProject getProject() {
        if (watchedProject == null && this.project != null) {
            ObjectifyDao<WatchedProject> projectDao = ObjectifyDao.newDao(WatchedProject.class);
            watchedProject = projectDao.get(this.project);
        }
        return watchedProject;
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

    public final boolean isActiveTask() {
        return activeTask;
    }

    public void setActiveTask(boolean active) {
        this.activeTask = active;
    }

    public final boolean isDeletedTask() {
        return deletedTask;
    }

    public void setDeletedTask(boolean deleted) {
        this.deletedTask = deleted;
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
    protected void prePersist() {
        modifiedDate = new Date();
    }

}
