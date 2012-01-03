package org.dodgybits.shuffle.server.model;

import java.util.List;

public class TaskQueryResult {

    private List<WatchedTask> mEntities;
    private int mTotalCount;
    private int mOffset;

    public List<WatchedTask> getEntities() {
        return mEntities;
    }

    public void setEntities(List<WatchedTask> entities) {
        mEntities = entities;
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(int totalCount) {
        mTotalCount = totalCount;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setOffset(int offset) {
        mOffset = offset;
    }

    @Override
    public String toString() {
        return String.format("TaskQueryResult entities=%s totalCount=%s offset=%d",
                mEntities, mTotalCount, mOffset);
    }

}
