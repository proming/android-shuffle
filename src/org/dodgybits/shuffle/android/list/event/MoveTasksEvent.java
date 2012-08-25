package org.dodgybits.shuffle.android.list.event;

import android.database.Cursor;
import com.google.common.collect.Sets;

import java.util.Set;

public class MoveTasksEvent {
    private Set<Long> mTaskIds;
    private boolean mMoveUp;
    private Cursor mCursor;
    
    public MoveTasksEvent(Set<Long> taskIds, boolean moveUp, Cursor cursor) {
        mTaskIds = taskIds;
        mMoveUp = moveUp;
        mCursor = cursor;
    }

    public MoveTasksEvent(Long taskId, boolean moveUp, Cursor cursor) {
        mTaskIds = Sets.newHashSet(taskId);
        mMoveUp = moveUp;
        mCursor = cursor;
    }

    public Set<Long> getTaskIds() {
        return mTaskIds;
    }

    public boolean isMoveUp() {
        return mMoveUp;
    }

    public Cursor getCursor() {
        return mCursor;
    }
}
