package org.dodgybits.shuffle.android.core.model.encoding;

import android.os.Bundle;
import com.google.inject.Singleton;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Context.Builder;

import static android.provider.BaseColumns._ID;
import static org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider.ShuffleTable.ACTIVE;
import static org.dodgybits.shuffle.android.persistence.provider.AbstractCollectionProvider.ShuffleTable.DELETED;
import static org.dodgybits.shuffle.android.persistence.provider.ContextProvider.Contexts.*;

@Singleton
public class ContextEncoder extends AbstractEntityEncoder implements EntityEncoder<Context> {

    @Override
    public void save(Bundle icicle, Context context) {
        putId(icicle, _ID, context.getLocalId());
        putId(icicle, TRACKS_ID, context.getTracksId());
        icicle.putLong(MODIFIED_DATE, context.getModifiedDate());
        icicle.putBoolean(DELETED, context.isDeleted());
        icicle.putBoolean(ACTIVE, context.isActive());

        putString(icicle, NAME, context.getName());
        icicle.putInt(COLOUR, context.getColourIndex());
        putString(icicle, ICON, context.getIconName());
    }
    
    @Override
    public Context restore(Bundle icicle) {
        if (icicle == null) return null;

        Builder builder = Context.newBuilder();
        builder.setLocalId(getId(icicle, _ID));
        builder.setModifiedDate(icicle.getLong(MODIFIED_DATE, 0L));
        builder.setTracksId(getId(icicle, TRACKS_ID));
        builder.setDeleted(icicle.getBoolean(DELETED));
        builder.setActive(icicle.getBoolean(ACTIVE));

        builder.setName(getString(icicle, NAME));
        builder.setColourIndex(icicle.getInt(COLOUR));
        builder.setIconName(getString(icicle, ICON));

        return builder.build();
    }

    
}
