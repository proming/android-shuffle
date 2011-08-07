package org.dodgybits.shuffle.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long mId;

    @Version
    @Column(name = "version")
    private Integer mVersion;
    
    String mDescription;
    String mDetails;
    Date mCreatedDate;
    Date mModifiedDate;
    boolean mActive = true;
    boolean mDeleted;
    // 0-indexed order within a project.
    int mOrder;
    boolean mComplete;

    
    public final Long getId() {
        return mId;
    }
    public final void setId(Long id) {
        mId = id;
    }
    public final Integer getVersion() {
        return mVersion;
    }
    public final void setVersion(Integer version) {
        mVersion = version;
    }
    public final String getDescription() {
        return mDescription;
    }
    public final void setDescription(String description) {
        mDescription = description;
    }
    public final String getDetails() {
        return mDetails;
    }
    public final void setDetails(String details) {
        mDetails = details;
    }
    public final Date getCreatedDate() {
        return mCreatedDate;
    }
    public final void setCreatedDate(Date createdDate) {
        mCreatedDate = createdDate;
    }
    public final Date getModifiedDate() {
        return mModifiedDate;
    }
    public final void setModifiedDate(Date modifiedDate) {
        mModifiedDate = modifiedDate;
    }
    public final boolean isActive() {
        return mActive;
    }
    public final void setActive(boolean active) {
        mActive = active;
    }
    public final boolean isDeleted() {
        return mDeleted;
    }
    public final void setDeleted(boolean deleted) {
        mDeleted = deleted;
    }
    public final int getOrder() {
        return mOrder;
    }
    public final void setOrder(int order) {
        mOrder = order;
    }
    public final boolean isComplete() {
        return mComplete;
    }
    public final void setComplete(boolean complete) {
        mComplete = complete;
    }

    
    
}
