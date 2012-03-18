package org.dodgybits.shuffle.android.list.view.project;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.persistence.ProjectPersister;
import roboguice.inject.ContextScopedProvider;

public class ProjectListAdaptor extends CursorAdapter {

    private final ProjectPersister mPersister;

    private final ContextScopedProvider<ProjectListItem> mProjectListItemProvider;

    private SparseIntArray mTaskCountArray;


    @Inject
    public ProjectListAdaptor(Context context, ProjectPersister persister,
                              ContextScopedProvider<ProjectListItem> ProjectListItemProvider
    ) {
        super(context, null, 0 /* no auto requery */);
        mPersister = persister;
        mProjectListItemProvider = ProjectListItemProvider;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mProjectListItemProvider.get(context);
    }

    @Override
    public void bindView(View view, Context androidContext, Cursor cursor) {
        // Reset the view (in case it was recycled) and prepare for binding
        ProjectListItem itemView = (ProjectListItem) view;
        Project project = mPersister.read(cursor);
        itemView.setTaskCountArray(mTaskCountArray);
        itemView.updateView(project);
    }

    public void setTaskCountArray(SparseIntArray taskCountArray) {
        mTaskCountArray = taskCountArray;
    }

}
