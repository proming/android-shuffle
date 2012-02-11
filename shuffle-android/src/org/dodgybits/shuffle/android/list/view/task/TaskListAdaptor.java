package org.dodgybits.shuffle.android.list.view.task;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.util.CollectionUtils;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import roboguice.inject.ContextScopedProvider;

import java.util.HashSet;
import java.util.Set;

public class TaskListAdaptor extends CursorAdapter {
    private static final String STATE_CHECKED_ITEMS =
            "org.dodgybits.shuffle.android.list.view.task.TaskListAdaptor.checkedItems";

    /**
     * Set of selected task IDs.
     */
    private final HashSet<Long> mSelectedSet = new HashSet<Long>();

    private final TaskPersister mPersister;

    private final ContextScopedProvider<TaskListItem> mTaskListItemProvider;

    /**
     * Callback from MessageListAdapter.  All methods are called on the UI thread.
     */
    public interface Callback {
        /** Called when the user selects/unselects a task */
        void onAdapterSelectedChanged(TaskListItem itemView, boolean newSelected,
                                      int mSelectedCount);
    }

    private Callback mCallback;

    @Inject
    public TaskListAdaptor(Context context, TaskPersister persister,
                           ContextScopedProvider<TaskListItem> taskListItemProvider) {
        super(context.getApplicationContext(), null, 0 /* no auto requery */);
        mPersister = persister;
        mTaskListItemProvider = taskListItemProvider;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putLongArray(STATE_CHECKED_ITEMS, CollectionUtils.toPrimitiveLongArray(getSelectedSet()));
    }

    public void loadState(Bundle savedInstanceState) {
        Set<Long> checkedset = getSelectedSet();
        checkedset.clear();
        for (long l: savedInstanceState.getLongArray(STATE_CHECKED_ITEMS)) {
            checkedset.add(l);
        }
        notifyDataSetChanged();
    }

    public Set<Long> getSelectedSet() {
        return mSelectedSet;
    }

    /**
     * Clear the selection.  It's preferable to calling {@link Set#clear()} on
     * {@link #getSelectedSet()}, because it also notifies observers.
     */
    public void clearSelection() {
        Set<Long> checkedset = getSelectedSet();
        if (checkedset.size() > 0) {
            checkedset.clear();
            notifyDataSetChanged();
        }
    }

    public boolean isSelected(TaskListItem itemView) {
        return getSelectedSet().contains(itemView.mTaskId);
    }
    
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Reset the view (in case it was recycled) and prepare for binding
        TaskListItem itemView = (TaskListItem) view;
        itemView.bindViewInit(this);

        Task task = mPersister.read(cursor);
        itemView.setTask(task);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        TaskListItem item = mTaskListItemProvider.get(context);
        item.setVisibility(View.VISIBLE);
        return item;
    }

    public void toggleSelected(TaskListItem itemView) {
        updateSelected(itemView, !isSelected(itemView));
    }

    /**
     * This is used as a callback from the list items, to set the selected state
     *
     * <p>Must be called on the UI thread.
     *
     * @param itemView the item being changed
     * @param newSelected the new value of the selected flag (checkbox state)
     */
    private void updateSelected(TaskListItem itemView, boolean newSelected) {
        if (newSelected) {
            mSelectedSet.add(itemView.mTaskId);
        } else {
            mSelectedSet.remove(itemView.mTaskId);
        }
        if (mCallback != null) {
            mCallback.onAdapterSelectedChanged(itemView, newSelected, mSelectedSet.size());
        }
    }



    /**
     * Creates the loader for {@link TaskListFragment}.
     *
     * @return always of {@link Cursor}.
     */
    public static Loader<Cursor> createLoader(Context context, TaskListContext listContext) {
        return new TaskCursorLoader(context, listContext);
    }

    private static class TaskCursorLoader extends CursorLoader {
        protected final Context mContext;

        private TaskSelector mSelector;
        
        public TaskCursorLoader(Context context, TaskListContext listContext) {
            // Initialize with no where clause.  We'll set it later.
            super(context, TaskProvider.Tasks.CONTENT_URI,
                    TaskProvider.Tasks.FULL_PROJECTION, null, null,
                    null);
            mSelector = listContext.createSelectorWithPreferences(context);
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
}
