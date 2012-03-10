package org.dodgybits.shuffle.android.core.model.encoding;

import android.os.Bundle;
import com.google.inject.Singleton;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Project.Builder;

import static android.provider.BaseColumns._ID;
import static org.dodgybits.shuffle.android.persistence.provider.ProjectProvider.Projects.*;

@Singleton
public class ProjectEncoder extends AbstractEntityEncoder implements EntityEncoder<Project> {

    @Override
    public void save(Bundle icicle, Project project) {
        putId(icicle, _ID, project.getLocalId());
        putId(icicle, TRACKS_ID, project.getTracksId());
        icicle.putLong(MODIFIED_DATE, project.getModifiedDate());
        icicle.putBoolean(DELETED, project.isDeleted());
        icicle.putBoolean(ACTIVE, project.isActive());

        putString(icicle, NAME, project.getName());
        putId(icicle, DEFAULT_CONTEXT_ID, project.getDefaultContextId());
        icicle.putBoolean(ARCHIVED, project.isArchived());
        icicle.putBoolean(PARALLEL, project.isParallel());
    }
    
    @Override
    public Project restore(Bundle icicle) {
        if (icicle == null) return null;

        Builder builder = Project.newBuilder();
        builder.setLocalId(getId(icicle, _ID));
        builder.setModifiedDate(icicle.getLong(MODIFIED_DATE, 0L));
        builder.setTracksId(getId(icicle, TRACKS_ID));
        builder.setDeleted(icicle.getBoolean(DELETED));
        builder.setActive(icicle.getBoolean(ACTIVE));

        builder.setName(getString(icicle, NAME));
        builder.setDefaultContextId(getId(icicle, DEFAULT_CONTEXT_ID));
        builder.setArchived(icicle.getBoolean(ARCHIVED));
        builder.setParallel(icicle.getBoolean(PARALLEL));
        return builder.build();
    }
    
}
