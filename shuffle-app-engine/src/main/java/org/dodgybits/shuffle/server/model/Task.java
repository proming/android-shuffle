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
import java.util.Date;
import java.util.List;
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

    @NotSaved(IfDefault.class)
    private boolean mAllDay = false;
    // 0-indexed order within a project.
    @Indexed()
    protected int order;
    @Indexed
    private boolean complete;

    @NotSaved(IfDefault.class)
    private boolean deletedTask = false;

    @NotSaved(IfDefault.class)
    private boolean activeTask = true;

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
        setContextKeys(Lists.newArrayList(Lists.transform(ids, new Function<Long, Key<Context>>() {
            @Override
            public Key<Context> apply(@Nullable Long input) {
                return new Key<Context>(Context.class, input);
            }
        })));
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
            setProjectKey(null);
        } else {
            setProjectKey(new Key<Project>(Project.class, id));
        }
    }
    
    public Project getProject() {
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

    public void setActive(boolean active) {
        this.activeTask = active;
    }

    public final boolean isDeleted() {
        return deletedTask;
    }

    public void setDeleted(boolean deleted) {
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
    private void PrePersist() {
        modifiedDate = new Date();
    }

}
