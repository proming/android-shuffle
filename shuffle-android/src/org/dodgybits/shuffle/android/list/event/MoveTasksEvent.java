package org.dodgybits.shuffle.android.list.event;

import com.google.common.collect.Sets;

import java.util.Set;

public class MoveTasksEvent {
    private Set<Long> mTaskIds;
    private boolean mMoveUp;

    public MoveTasksEvent(Set<Long> taskIds, boolean moveUp) {
        mTaskIds = taskIds;
        mMoveUp = moveUp;
    }

    public MoveTasksEvent(Long taskId, boolean moveUp) {
        mTaskIds = Sets.newHashSet(taskId);
        mMoveUp = moveUp;
    }

    public Set<Long> getTaskIds() {
        return mTaskIds;
    }

    public boolean isMoveUp() {
        return mMoveUp;
    }
}
