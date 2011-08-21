package org.dodgybits.shuffle.server.model;

import java.util.Date;

import org.dodgybits.shuffle.server.service.AppUserDao;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@Entity
public class Task extends DatastoreObject {
    String description;
    String details;
    Date createdDate;
    Date modifiedDate;
    boolean active = true;
    boolean deleted;
    // 0-indexed order within a project.
    int order;
    boolean complete;
    private Key<AppUser> owner;

    
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
    public final void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
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

    public AppUser getOwner()
    {
        try
        {
            return new AppUserDao().get(owner);
        } catch (EntityNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setOwner(AppUser owner)
    {
        this.owner = new AppUserDao().key(owner);
    }
    
    
}
