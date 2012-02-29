package org.dodgybits.shuffle.android.list.view.context;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarFragmentActivity;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.activity.ContextTaskListsActivity;
import org.dodgybits.shuffle.android.list.content.ContextCursorLoader;
import org.dodgybits.shuffle.android.list.event.*;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.model.ListSettingsCache;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import roboguice.event.EventManager;
import roboguice.fragment.RoboListFragment;

public class ContextListFragment extends RoboListFragment {
    private static final String TAG = "ContextListFragment";
    
    /** Argument name(s) */
    private static final String BUNDLE_LIST_STATE = "ContextListFragment.state.listState";
    private static final String SELECTED_ITEM = "SELECTED_ITEM";

    // result codes
    private static final int FILTER_CONFIG = 600;

    private static final int LOADER_ID_CONTEXT_LIST_LOADER = 1;
    private static final int LOADER_ID_TASK_COUNT_LOADER = 2;

    @Inject
    private ContextListAdaptor mListAdapter;

    @Inject
    private TaskPersister mTaskPersister;
    
    @Inject
    private ContextPersister mContextPersister;

    @Inject
    private EventManager mEventManager;
    
    /**
     * {@link android.view.ActionMode} shown when 1 or more message is selected.
     */
//    private ActionMode mSelectionMode;
//    private SelectionModeCallback mLastSelectionModeCallback;

    private Parcelable mSavedListState;

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView lv = getListView();
        lv.setItemsCanFocus(false);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(lv);

        setEmptyText(getString(R.string.no_contexts));

        if (savedInstanceState != null) {
            // Fragment doesn't have this method.  Call it manually.
            restoreInstanceState(savedInstanceState);
        }

        startLoading();

        Log.d(TAG, "-onActivityCreated");
    }

    @Override
    public void onPause() {
        mSavedListState = getListView().onSaveInstanceState();
        super.onPause();

        Log.d(TAG, "-onPause");
    }

    @Override
    public void onResume() {
        super.onResume();

        onVisibilityChange();
        refreshChildCount();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        onVisibilityChange();
    }

    /**
     * Called when a message is clicked.
     */
    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {

        String action = getActivity().getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action)
                || Intent.ACTION_GET_CONTENT.equals(action)) {
            // The caller is waiting for us to return a task selected by
            // the user. They have clicked on one, so return it now.
            Uri url = ContentUris.withAppendedId(ContextProvider.Contexts.CONTENT_URI, id);
            Intent intent = new Intent();
            intent.putExtra(SELECTED_ITEM, url.toString());
            getActivity().setResult(Activity.RESULT_OK, intent);
        } else {
            Intent intent = new Intent(getActivity(), ContextTaskListsActivity.class);
            intent.putExtra(ContextTaskListsActivity.INITIAL_POSITION, position);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Log.d(TAG, "adding task");
                mEventManager.fire(new NewContextEvent());
                return true;
            case R.id.action_help:
                Log.d(TAG, "Bringing up help");
                mEventManager.fire(new ViewHelpEvent(ListQuery.context));
                return true;
            case R.id.action_view_settings:
                Log.d(TAG, "Bringing up view settings");
                mEventManager.fire(new EditListSettingsEvent(ListQuery.context, this, FILTER_CONFIG));
                return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        Log.d(TAG, "Got resultCode " + resultCode + " with data " + data);
        switch (requestCode) {
            case FILTER_CONFIG:
                restartLoading();
                break;

            default:
                Log.e(TAG, ("Unknown requestCode: " + requestCode));
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_list_context_menu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        org.dodgybits.shuffle.android.core.model.Context context = mContextPersister.read(cursor);
        menu.findItem(R.id.action_delete).setVisible(!context.isDeleted());
        menu.findItem(R.id.action_undelete).setVisible(context.isDeleted());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (!getUserVisibleHint()) return super.onContextItemSelected(item);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_edit:
                mEventManager.fire(new EditContextEvent(Id.create(info.id)));
                return true;
            case R.id.action_delete:
                mEventManager.fire(new UpdateContextDeletedEvent(Id.create(info.id), true));
                restartLoading();
                return true;
            case R.id.action_undelete:
                mEventManager.fire(new UpdateContextDeletedEvent(Id.create(info.id), false));
                restartLoading();
                return true;
        }

        return super.onContextItemSelected(item);
    }


    private void onVisibilityChange() {
        if (getUserVisibleHint()) {
            updateTitle();
            ((ActionBarFragmentActivity)getActivity()).supportResetOptionsMenu();
        }
    }


    private void updateTitle() {
        getActivity().setTitle(R.string.title_context);
    }

    void restoreInstanceState(Bundle savedInstanceState) {
        mSavedListState = savedInstanceState.getParcelable(BUNDLE_LIST_STATE);
    }

    private void startLoading() {
        Log.d(TAG, "Creating list cursor");
        final LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID_CONTEXT_LIST_LOADER, null, LOADER_CALLBACKS);
    }

    private void restartLoading() {
        Log.d(TAG, "Refreshing list cursor");
        final LoaderManager lm = getLoaderManager();
        lm.restartLoader(LOADER_ID_CONTEXT_LIST_LOADER, null, LOADER_CALLBACKS);
    }

    private void refreshChildCount() {
        Log.d(TAG, "Refreshing list cursor");
        final LoaderManager lm = getLoaderManager();
        lm.restartLoader(LOADER_ID_TASK_COUNT_LOADER, null, LOADER_COUNT_CALLBACKS);
    }


    /**
     * Loader callbacks for message list.
     */
    private final LoaderManager.LoaderCallbacks<Cursor> LOADER_CALLBACKS =
            new LoaderManager.LoaderCallbacks<Cursor>() {

                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    return new ContextCursorLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
                    // Update the list
                    mListAdapter.swapCursor(c);
                    setListAdapter(mListAdapter);

                    // Restore the state -- this step has to be the last, because Some of the
                    // "post processing" seems to reset the scroll position.
                    if (mSavedListState != null) {
                        getListView().onRestoreInstanceState(mSavedListState);
                        mSavedListState = null;
                    }
                }


                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    mListAdapter.swapCursor(null);
                }
            };

    /**
     * Loader callbacks for task counts.
     */
    private final LoaderManager.LoaderCallbacks<Cursor> LOADER_COUNT_CALLBACKS =
            new LoaderManager.LoaderCallbacks<Cursor>() {

                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    return new TaskCountCursorLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    mListAdapter.setTaskCountArray(mTaskPersister.readCountArray(cursor));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getListView().invalidateViews();
                        }
                    });
                    cursor.close();
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                }
            };

    private static class TaskCountCursorLoader extends CursorLoader {
        protected final Context mContext;

        private TaskSelector mSelector;

        public TaskCountCursorLoader(Context context) {
            // Initialize with no where clause.  We'll set it later.
            super(context, ContextProvider.Contexts.CONTEXT_TASKS_CONTENT_URI,
                    ContextProvider.Contexts.FULL_TASK_PROJECTION, null, null,
                    null);
            mSelector = TaskSelector.newBuilder().applyListPreferences(context,
                    ListSettingsCache.findSettings(ListQuery.context)).build();
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