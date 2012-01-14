package org.dodgybits.shuffle.server.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.NotSaved;
import com.googlecode.objectify.annotation.Unindexed;
import com.googlecode.objectify.condition.IfDefault;

import javax.persistence.PrePersist;
import java.util.Date;

@Entity
@Unindexed
public class Project extends UserDatastoreObject {

    private Key<WatchedContext> defaultContext;

    @NotSaved(IfDefault.class)
    private boolean parallel = false;

    @NotSaved(IfDefault.class)
    private boolean archived = false;

    @Indexed
    private String name;

    @Indexed
    private Date modifiedDate;

    @Indexed
    private boolean deleted = false;

    @Indexed
    private boolean active = true;

    public Key<WatchedContext> getDefaultContextKey() {
        return defaultContext;
    }

    public void setDefaultContextKey(Key<WatchedContext> defaultContextKey) {
        this.defaultContext = defaultContextKey;
    }

    public Long getDefaultContextId() {
        Long id = null;
        if (defaultContext != null) {
            id = defaultContext.getId();
        }
        return id;
    }

    public void setDefaultContextId(Long contextId) {
        if (contextId == null) {
            defaultContext = null;
        } else {
            defaultContext = new Key<WatchedContext>(WatchedContext.class, contextId);
        }
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final Date getModifiedDate() {
        return modifiedDate;
    }

    public final boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public final boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @PrePersist
    protected void prePersist() {
        modifiedDate = new Date();
    }

}
