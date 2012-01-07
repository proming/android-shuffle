package org.dodgybits.shuffle.server.model;

import javax.persistence.Transient;

public class WatchedContext extends Context {

    @Transient
    private boolean activeChanged = false;
    @Transient
    private boolean deletedChanged = false;

    @Override
    public void setActive(boolean active) {
        if (active != isActive()) {
            super.setActive(active);
            activeChanged = true;
        }
    }

    @Override
    public void setDeleted(boolean deleted) {
        if (deleted != isDeleted()) {
            super.setDeleted(deleted);
            deletedChanged = true;
        }
    }

    public boolean isActiveChanged() {
        return activeChanged;
    }

    public boolean isDeletedChanged() {
        return deletedChanged;
    }

    @Override
    protected void prePersist() {
        super.prePersist();
        activeChanged = deletedChanged = false;
    }

}
