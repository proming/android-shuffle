package org.dodgybits.shuffle.server.model;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import org.dodgybits.shuffle.server.service.AppUserDao;

import javax.persistence.PrePersist;
import java.util.Date;

@Entity
public class Context extends UserDatastoreObject {
    private String name;
    private int colourIndex;
    private String iconName;
    private Date modifiedDate;
    private boolean deleted;
    private boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColourIndex() {
        return colourIndex;
    }

    public void setColourIndex(int colourIndex) {
        this.colourIndex = colourIndex;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
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
