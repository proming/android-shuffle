package org.dodgybits.shuffle.android.core.model.encoding;

import android.os.Bundle;
import com.google.inject.Singleton;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.Task.Builder;

import static android.provider.BaseColumns._ID;
import static org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider.ShuffleTable.ACTIVE;
import static org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider.ShuffleTable.DELETED;
import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.*;

@Singleton
public class TaskEncoder extends AbstractEntityEncoder implements
        EntityEncoder<Task> {
    
    @Override
    public void save(Bundle icicle, Task task) {
        putId(icicle, _ID, task.getLocalId());
        putId(icicle, TRACKS_ID, task.getTracksId());
        icicle.putLong(MODIFIED_DATE, task.getModifiedDate());
        icicle.putBoolean(DELETED, task.isDeleted());
        icicle.putBoolean(ACTIVE, task.isActive());

        putString(icicle, DESCRIPTION, task.getDescription());
        putString(icicle, DETAILS, task.getDetails());
        putId(icicle, CONTEXT_ID, task.getContextId());
        putId(icicle, PROJECT_ID, task.getProjectId());
        icicle.putLong(CREATED_DATE, task.getCreatedDate());
        icicle.putLong(START_DATE, task.getStartDate());
        icicle.putLong(DUE_DATE, task.getDueDate());
        putString(icicle, TIMEZONE, task.getTimezone());
        putId(icicle, CAL_EVENT_ID, task.getCalendarEventId());
        icicle.putBoolean(ALL_DAY, task.isAllDay());
        icicle.putBoolean(HAS_ALARM, task.hasAlarms());
        icicle.putInt(DISPLAY_ORDER, task.getOrder());
        icicle.putBoolean(COMPLETE, task.isComplete());
    }

    @Override
    public Task restore(Bundle icicle) {
        if (icicle == null) return null;

        Builder builder = Task.newBuilder();
        builder.setLocalId(getId(icicle, _ID));
        builder.setModifiedDate(icicle.getLong(MODIFIED_DATE, 0L));
        builder.setTracksId(getId(icicle, TRACKS_ID));
        builder.setDeleted(icicle.getBoolean(DELETED));
        builder.setActive(icicle.getBoolean(ACTIVE));

        builder.setDescription(getString(icicle, DESCRIPTION));
        builder.setDetails(getString(icicle, DETAILS));
        builder.setContextId(getId(icicle, CONTEXT_ID));
        builder.setProjectId(getId(icicle, PROJECT_ID));
        builder.setCreatedDate(icicle.getLong(CREATED_DATE, 0L));
        builder.setStartDate(icicle.getLong(START_DATE, 0L));
        builder.setDueDate(icicle.getLong(DUE_DATE, 0L));
        builder.setTimezone(getString(icicle, TIMEZONE));
        builder.setCalendarEventId(getId(icicle, CAL_EVENT_ID));
        builder.setAllDay(icicle.getBoolean(ALL_DAY));
        builder.setHasAlarm(icicle.getBoolean(HAS_ALARM));
        builder.setOrder(icicle.getInt(DISPLAY_ORDER));
        builder.setComplete(icicle.getBoolean(COMPLETE));

        return builder.build();
    }

}
