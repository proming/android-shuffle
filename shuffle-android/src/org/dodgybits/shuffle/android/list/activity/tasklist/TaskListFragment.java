package org.dodgybits.shuffle.android.list.activity.tasklist;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.util.UiUtilities;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import roboguice.fragment.RoboListFragment;

import java.util.Set;

public class TaskListFragment extends RoboListFragment
        implements AdapterView.OnItemLongClickListener, TaskListAdaptor.Callback {

    /** Argument name(s) */
    private static final String ARG_LIST_CONTEXT = "listContext";
    private static final String BUNDLE_LIST_STATE = "TaskListFragment.state.listState";
    public static final String SELECTED_ITEM = "SELECTED_ITEM";

    private static final int LOADER_ID_MESSAGES_LOADER = 1;

    private TaskListAdaptor mListAdapter;
    private boolean mIsFirstLoad;

    /**
     * {@link ActionMode} shown when 1 or more message is selected.
     */
//    private ActionMode mSelectionMode;
//    private SelectionModeCallback mLastSelectionModeCallback;

    private Parcelable mSavedListState;

    // UI Support
    private Activity mActivity;
    private boolean mIsViewCreated;

    @Inject
    private TaskPersister mPersister;

    /**
     * Create a new instance with initialization parameters.
     *
     * This fragment should be created only with this method.  (Arguments should always be set.)
     *
     * @param listContext The list context to show tasks for
     */
    public static TaskListFragment newInstance(TaskListContext listContext) {
        final TaskListFragment instance = new TaskListFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_LIST_CONTEXT, listContext);
        instance.setArguments(args);
        return instance;
    }

    /**
     * The context describing the contents to be shown in the list.
     * Do not use directly; instead, use the getters.
     * <p><em>NOTE:</em> Although we cannot force these to be immutable using Java language
     * constructs, this <em>must</em> be considered immutable.
     */
    private TaskListContext mListContext;

    private void initializeArgCache() {
        if (mListContext != null) return;
        mListContext = getArguments().getParcelable(ARG_LIST_CONTEXT);
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        setHasOptionsMenu(true);

        mListAdapter = new TaskListAdaptor(mActivity, this, mPersister);
        mIsFirstLoad = true;

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Use a custom layout, which includes the original layout with "send messages" panel.
        View root = inflater.inflate(R.layout.task_list_fragment,null);
        mIsViewCreated = true;
        return root;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView lv = getListView();
        lv.setOnItemLongClickListener(this);
        lv.setItemsCanFocus(false);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

//        setEmptyText(getString(R.string.no_tasks));

        if (savedInstanceState != null) {
            // Fragment doesn't have this method.  Call it manually.
            restoreInstanceState(savedInstanceState);
        }

        startLoading();

        UiUtilities.installFragment(this);
    }

    @Override
    public void onPause() {
        mSavedListState = getListView().onSaveInstanceState();
        super.onPause();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // Always toggle the item.
        TaskListItem listItem = (TaskListItem) view;
        boolean toggled = false;
        if (!mListAdapter.isSelected(listItem)) {
            toggleSelection(listItem);
            toggled = true;
        }

        return toggled;
    }

    /**
     * Called when a message is clicked.
     */
    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
        Uri url = ContentUris.withAppendedId(TaskProvider.Tasks.CONTENT_URI, id);

        String action = getActivity().getIntent().getAction();
        if (Intent.ACTION_PICK.equals(action)
                || Intent.ACTION_GET_CONTENT.equals(action)) {
            // The caller is waiting for us to return a task selected by
            // the user. They have clicked on one, so return it now.
            Bundle bundle = new Bundle();
            bundle.putString(SELECTED_ITEM, url.toString());
            Intent mIntent = new Intent();
            mIntent.putExtras(bundle);
            getActivity().setResult(Activity.RESULT_OK, mIntent);
        } else {
            // Launch activity to view/edit the currently selected item
            startActivity(new Intent(Intent.ACTION_VIEW, url));
        }
    }

    @Override
    public void onAdapterSelectedChanged(TaskListItem itemView, boolean newSelected, int mSelectedCount) {
        updateSelectionMode();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mListAdapter.onSaveInstanceState(outState);
    }


    void restoreInstanceState(Bundle savedInstanceState) {
        mListAdapter.loadState(savedInstanceState);
        mSavedListState = savedInstanceState.getParcelable(BUNDLE_LIST_STATE);
    }

    private void startLoading() {
        // Start loading...
        final LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID_MESSAGES_LOADER, null, LOADER_CALLBACKS);
    }


    public TaskListContext getListContext() {
        initializeArgCache();
        return mListContext;
    }

    /**
     * Loader callbacks for message list.
     */
    private final LoaderManager.LoaderCallbacks<Cursor> LOADER_CALLBACKS =
            new LoaderManager.LoaderCallbacks<Cursor>() {

                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    final TaskListContext listContext = getListContext();

                    mIsFirstLoad = true;
                    return TaskListAdaptor.createLoader(getActivity(), listContext);
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

                    mIsFirstLoad = false;
                }


                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    mListAdapter.swapCursor(null);
                }
            };

    /**
     * @return true if the content view is created and not destroyed yet. (i.e. between
     * {@link #onCreateView} and {@link #onDestroyView}.
     */
    private boolean isViewCreated() {
        // Note that we don't use "getView() != null".  This method is used in updateSelectionMode()
        // to determine if CAB shold be shown.  But because it's called from onDestroyView(), at
        // this point the fragment still has views but we want to hide CAB, we can't use
        // getView() here.
        return mIsViewCreated;
    }


    private void toggleSelection(TaskListItem itemView) {
        itemView.invalidate();
        mListAdapter.toggleSelected(itemView);
    }

    private void onDeselectAll() {
        mListAdapter.clearSelection();
        if (isInSelectionMode()) {
            finishSelectionMode();
        }
    }
    /**
     * Show/hide the "selection" action mode, according to the number of selected messages and
     * the visibility of the fragment.
     * Also update the content (title and menus) if necessary.
     */
    public void updateSelectionMode() {
        final int numSelected = getSelectedCount();
        if ((numSelected == 0) || !isViewCreated()) {
            finishSelectionMode();
            return;
        }
        if (isInSelectionMode()) {
            updateSelectionModeView();
        } else {
//            mLastSelectionModeCallback = new SelectionModeCallback();
//            getActivity().startActionMode(mLastSelectionModeCallback);
        }
    }


    /**
     * Finish the "selection" action mode.
     *
     * Note this method finishes the contextual mode, but does *not* clear the selection.
     * If you want to do so use {@link #onDeselectAll()} instead.
     */
    private void finishSelectionMode() {
        if (isInSelectionMode()) {
//            mLastSelectionModeCallback.mClosedByUser = false;
//            mSelectionMode.finish();
        }
    }

    /** Update the "selection" action mode bar */
    private void updateSelectionModeView() {
//        mSelectionMode.invalidate();
    }

    /**
     * @return the number of messages that are currently selected.
     */
    private int getSelectedCount() {
        return mListAdapter.getSelectedSet().size();
    }

    /**
     * @return true if the list is in the "selection" mode.
     */
    public boolean isInSelectionMode() {
//        return mSelectionMode != null;
        return false;
    }

    private class SelectionModeCallback implements ActionMode.Callback {
        private MenuItem mMarkRead;
        private MenuItem mMarkUnread;
        private MenuItem mAddStar;
        private MenuItem mRemoveStar;
        private MenuItem mMove;

        /* package */ boolean mClosedByUser = true;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            mSelectionMode = mode;

//            MenuInflater inflater = getActivity().getMenuInflater();
//            inflater.inflate(R.menu.message_list_fragment_cab_options, menu);
//            mMarkRead = menu.findItem(R.id.mark_read);
//            mMarkUnread = menu.findItem(R.id.mark_unread);
//            mAddStar = menu.findItem(R.id.add_star);
//            mRemoveStar = menu.findItem(R.id.remove_star);
//            mMove = menu.findItem(R.id.move);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            int num = getSelectedCount();
            // Set title -- "# selected"
//            mSelectionMode.setTitle(getActivity().getResources().getQuantityString(
//                    R.plurals.message_view_selected_message_count, num, num));
//
//            // Show appropriate menu items.
//            boolean nonStarExists = doesSelectionContainNonStarredMessage();
//            boolean readExists = doesSelectionContainReadMessage();
//            mMarkRead.setVisible(!readExists);
//            mMarkUnread.setVisible(readExists);
//            mAddStar.setVisible(nonStarExists);
//            mRemoveStar.setVisible(!nonStarExists);
//            mMove.setVisible(mShowMoveCommand);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Set<Long> selectedConversations = mListAdapter.getSelectedSet();
            if (selectedConversations.isEmpty()) return true;
            switch (item.getItemId()) {
//                case R.id.mark_read:
//                    // Note - marking as read does not trigger auto-advance.
//                    toggleRead(selectedConversations);
//                    break;
//                case R.id.mark_unread:
//                    mCallback.onAdvancingOpAccepted(selectedConversations);
//                    toggleRead(selectedConversations);
//                    break;
//                case R.id.add_star:
//                case R.id.remove_star:
//                    // TODO: removing a star can be a destructive command and cause auto-advance
//                    // if the current mailbox shown is favorites.
//                    toggleFavorite(selectedConversations);
//                    break;
//                case R.id.delete:
//                    mCallback.onAdvancingOpAccepted(selectedConversations);
//                    deleteMessages(selectedConversations);
//                    break;
//                case R.id.move:
//                    showMoveMessagesDialog(selectedConversations);
//                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // Clear this before onDeselectAll() to prevent onDeselectAll() from trying to close the
            // contextual mode again.
//            mSelectionMode = null;
            if (mClosedByUser) {
                // Clear selection, only when the contextual mode is explicitly closed by the user.
                //
                // We close the contextual mode when the fragment becomes temporary invisible
                // (i.e. mIsVisible == false) too, in which case we want to keep the selection.
                onDeselectAll();
            }
        }
    }

}
