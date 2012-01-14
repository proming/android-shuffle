package org.dodgybits.shuffle.server.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.PrePersist;
import java.util.Date;

@Entity
@Unindexed
public class Context extends UserDatastoreObject {
    @Indexed
    private String name;

    private int colourIndex;

    private String iconName;

    @Indexed
    private Date modifiedDate;

    @Indexed
    private boolean deleted = false;

    @Indexed
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
