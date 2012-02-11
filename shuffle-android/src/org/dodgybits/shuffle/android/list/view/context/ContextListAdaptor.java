package org.dodgybits.shuffle.android.list.view.context;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import roboguice.inject.ContextScopedProvider;

public class ContextListAdaptor extends CursorAdapter {

    private final ContextPersister mPersister;

    private final ContextScopedProvider<ContextListItem> mContextListItemProvider;

    private SparseIntArray mTaskCountArray;


    @Inject
    public ContextListAdaptor(Context context, ContextPersister persister,
                              ContextScopedProvider<ContextListItem> contextListItemProvider
    ) {
        super(context.getApplicationContext(), null, 0 /* no auto requery */);
        mPersister = persister;
        mContextListItemProvider = contextListItemProvider;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mContextListItemProvider.get(context);
    }

    @Override
    public void bindView(View view, Context androidContext, Cursor cursor) {
        // Reset the view (in case it was recycled) and prepare for binding
        ContextListItem itemView = (ContextListItem) view;
        org.dodgybits.shuffle.android.core.model.Context context = mPersister.read(cursor);
        itemView.setTaskCountArray(mTaskCountArray);
        itemView.updateView(context);
    }

    public void setTaskCountArray(SparseIntArray taskCountArray) {
        mTaskCountArray = taskCountArray;
    }

}
