package org.dodgybits.shuffle.server.model;

import org.dodgybits.shuffle.shared.Flag;
import org.dodgybits.shuffle.shared.PredefinedQuery;

public class TaskQuery {

    private Flag mActive;
    private Flag mDeleted;
    private int mCount;
    private int mOffset;
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

    public int getCount() {
        return mCount;
    }
    public void setCount(int value) {
        mCount = value;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setOffset(int offset) {
        mOffset = offset;
    }

    public PredefinedQuery getPredefinedQuery() {
        return mPredefinedQuery;
    }

    public void setPredefinedQuery(PredefinedQuery predefinedQuery) {
        mPredefinedQuery = predefinedQuery;
    }

    @Override
    public String toString() {
        return String.format("TaskQuery active=%s deleted=%s count=%d offset=%d predefined=%s",
                mActive, mDeleted, mCount, mOffset, mPredefinedQuery);
    }
}
