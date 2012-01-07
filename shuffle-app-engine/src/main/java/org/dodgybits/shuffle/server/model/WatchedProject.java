package org.dodgybits.shuffle.server.model;

import javax.persistence.Transient;

public class WatchedProject extends Project {

    @Transient
    private boolean activeChanged = false;
    @Transient
    private boolean deletedChanged = false;
    @Transient
    private boolean parallelChanged = false;

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

    @Override
    public void setParallel(boolean parallel) {
        if (parallel != isParallel()) {
            super.setParallel(parallel);
            parallelChanged = true;
        }
    }

    public boolean isActiveChanged() {
        return activeChanged;
    }

    public boolean isDeletedChanged() {
        return deletedChanged;
    }

    public boolean isParallelChanged() {
        return parallelChanged;
    }

    @Override
    protected void prePersist() {
        super.prePersist();
        activeChanged = deletedChanged = parallelChanged = false;
    }
}
