package org.dodgybits.shuffle.android.core.model.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Context.Builder;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import roboguice.inject.ContentResolverProvider;
import roboguice.inject.ContextSingleton;

import static org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider.ShuffleTable.ACTIVE;
import static org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider.ShuffleTable.DELETED;
import static org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider.ShuffleTable.MODIFIED_DATE;
import static org.dodgybits.shuffle.android.persistence.provider.ContextProvider.Contexts.*;
import static org.dodgybits.shuffle.android.persistence.provider.ContextProvider.Contexts.GAE_ID;

@ContextSingleton
public class ContextPersister extends AbstractEntityPersister<Context> {

    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int COLOUR_INDEX = 2;
    private static final int ICON_INDEX = 3;
    private static final int MODIFIED_INDEX = 4;
    private static final int DELETED_INDEX = 5;
    private static final int ACTIVE_INDEX = 6;
    private static final int GAE_ID_INDEX = 7;

    private final TaskPersister mTaskPersister;
            
    
    @Inject
    public ContextPersister(ContentResolverProvider provider, TaskPersister taskPersister) {
        super(provider.get());
        mTaskPersister = taskPersister;
    }

    @Override
    public Context read(Cursor cursor) {
        Builder builder = Context.newBuilder();
        builder
            .setLocalId(readId(cursor, ID_INDEX))
            .setModifiedDate(cursor.getLong(MODIFIED_INDEX))
            .setName(readString(cursor, NAME_INDEX))
            .setColourIndex(cursor.getInt(COLOUR_INDEX))
            .setIconName(readString(cursor, ICON_INDEX))
            .setDeleted(readBoolean(cursor, DELETED_INDEX))
            .setActive(readBoolean(cursor, ACTIVE_INDEX))
            .setGaeId(readId(cursor, GAE_ID_INDEX));

        return builder.build();
    }

    @Override
    protected void writeContentValues(ContentValues values, Context context) {
        // never write id since it's auto generated
        values.put(MODIFIED_DATE, context.getModifiedDate());
        writeString(values, NAME, context.getName());
        values.put(COLOUR, context.getColourIndex());
        writeString(values, ICON, context.getIconName());
        writeBoolean(values, DELETED, context.isDeleted());
        writeBoolean(values, ACTIVE, context.isActive());
        writeId(values, GAE_ID, context.getGaeId());
    }

    @Override
    public boolean updateDeletedFlag(Id id, boolean isDeleted) {
        boolean result = super.updateDeletedFlag(id, isDeleted);
        
        if (isDeleted) {
            mTaskPersister.removeTasksForContext(id);
        }

        return result;
    }

    @Override
    protected void update(Uri uri, Context context) {
        super.update(uri, context);

        if (context.isDeleted()) {
            mTaskPersister.removeTasksForContext(context.getLocalId());
        }
    }

    @Override
    protected String getEntityName() {
        return "context";
    }
    
    @Override
    public Uri getContentUri() {
        return ContextProvider.Contexts.CONTENT_URI;
    }

    @Override
    public String[] getFullProjection() {
        return ContextProvider.Contexts.FULL_PROJECTION;
    }


}
