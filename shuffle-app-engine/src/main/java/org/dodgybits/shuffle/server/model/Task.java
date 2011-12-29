package org.dodgybits.shuffle.server.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

import javax.annotation.Nullable;
import javax.persistence.PrePersist;
import java.util.Date;
import java.util.List;

@Entity
@Unindexed
public class Task extends UserDatastoreObject {
    
    private String description;
    private String details;
    private List<Key<Context>> contexts = Lists.newArrayList();
    private Key<Project> project;
    private Date createdDate;
    private Date modifiedDate;
    private Date showFromDate;
    private Date dueDate;
    private boolean mAllDay;
    private boolean active = true;
    private boolean deleted;
    // 0-indexed order within a project.
    private int order;
    private boolean complete;

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
        contexts = Lists.transform(ids, new Function<Long, Key<Context>>() {
            @Override
            public Key<Context> apply(@Nullable Long input) {
                return new Key<Context>(Context.class, input);
            }
        });
    }

    public Key<Project> getProjectKey() {
        return project;
    }

    public void setProjectKey(Key<Project> project) {
        this.project = project;
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
            project = null;
        } else {
            project = new Key<Project>(Project.class, id);
        }
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
        return details;
    }

    public final void setDetails(String details) {
        this.details = details;
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
        return active;
    }

    public final void setActive(boolean active) {
        this.active = active;
    }

    public final boolean isDeleted() {
        return deleted;
    }

    public final void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
