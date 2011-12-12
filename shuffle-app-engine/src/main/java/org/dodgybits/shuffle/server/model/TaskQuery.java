package org.dodgybits.shuffle.server.model;

import org.dodgybits.shuffle.shared.Flag;
import org.dodgybits.shuffle.shared.PredefinedQuery;

public class TaskQuery {

    private Flag mActive;
    private Flag mDeleted;
    private PredefinedQuery mPredefinedQuery;

    public Flag getActive() {
        return mActive;
    }

    public void setActive(Flag value) {
        mActive = value;
    }

    public Flag getDeleted() {
        return mDeleted;
    }
    public void setDeleted(Flag value) {
        mDeleted = value;
    }

    public PredefinedQuery getPredefinedQuery() {
        return mPredefinedQuery;
    }

    public void setPredefinedQuery(PredefinedQuery predefinedQuery) {
        mPredefinedQuery = predefinedQuery;
    }

    @Override
    public String toString() {
        return String.format("TaskQuery active=%s deleted=%s predefined=%s",
                mActive, mDeleted, mPredefinedQuery);
    }
}
