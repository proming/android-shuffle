package org.dodgybits.shuffle.android.list.content;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import org.dodgybits.shuffle.android.core.model.persistence.selector.ProjectSelector;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.model.ListSettingsCache;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;

public class ProjectCursorLoader extends CursorLoader {
    protected final Context mContext;

    private ProjectSelector mSelector;

    public ProjectCursorLoader(Context context) {
        // Initialize with no where clause.  We'll set it later.
        super(context, ProjectProvider.Projects.CONTENT_URI,
                ProjectProvider.Projects.FULL_PROJECTION, null, null,
                null);
        mSelector = ProjectSelector.newBuilder().applyListPreferences(context,
                ListSettingsCache.findSettings(ListQuery.project)).build();
        mContext = context;
    }

    @Override
    public Cursor loadInBackground() {
        // Build the where cause (which can't be done on the UI thread.)
        setSelection(mSelector.getSelection(mContext));
        setSelectionArgs(mSelector.getSelectionArgs());
        setSortOrder(mSelector.getSortOrder());
        // Then do a query to get the cursor
        return super.loadInBackground();
    }

}
