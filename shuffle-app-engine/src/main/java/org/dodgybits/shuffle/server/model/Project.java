package org.dodgybits.shuffle.server.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.PrePersist;
import java.util.Date;

@Entity
@Unindexed
public class Project extends UserDatastoreObject {
    private Key<Context> defaultContext;
    private boolean parallel;
    private boolean archived;
    @Indexed
    private String name;
    private Date modifiedDate;
    private boolean deleted;
    private boolean active = true;

    public Key<Context> getDefaultContextKey() {
        return defaultContext;
    }

    public void setDefaultContextKey(Key<Context> defaultContextKey) {
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
            defaultContext = new Key<Context>(Context.class, contextId);
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

    public final void setActive(boolean active) {
        this.active = active;
    }

    public final boolean isDeleted() {
        return deleted;
    }

    public final void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @PrePersist
    private void PrePersist() {
        modifiedDate = new Date();
    }


}
