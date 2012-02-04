package org.dodgybits.shuffle.android.view.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.encoding.TaskEncoder;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.activity.tasklist.TaskListContext;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import roboguice.activity.RoboFragmentActivity;

/**
 * View a task  in the context of a given task list.
 * Each task is a separate page on a PageViewer.
 */
public class TaskPagerActivity extends RoboFragmentActivity {
    private static final String cTag = "EntityListsActivity";

    public static final String SELECTED_INDEX = "selectedIndex";
    public static final String TASK_LIST_CONTEXT = "taskListContext";

    private static final int LOADER_ID_TASK_LIST_LOADER = 1;

    @Inject
    TaskPersister mPersister;

    @Inject
    TaskEncoder mEncoder;

    MyAdapter mAdapter;

    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mPager = (ViewPager)findViewById(R.id.pager);

        startLoading(getIntent().getExtras());
    }

    private void startLoading(Bundle args) {
        Log.d(cTag, "Creating list cursor");
        final LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(LOADER_ID_TASK_LIST_LOADER, args, LOADER_CALLBACKS);
    }


    /**
     * Loader callbacks for message list.
     */
    private final LoaderManager.LoaderCallbacks<Cursor> LOADER_CALLBACKS =
            new LoaderManager.LoaderCallbacks<Cursor>() {

                int initialPosition;
                
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    initialPosition = args.getInt(SELECTED_INDEX, 0);
                    TaskListContext listContext = args.getParcelable(TASK_LIST_CONTEXT);
                    return new TaskCursorLoader(TaskPagerActivity.this, listContext);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
                    mAdapter = new MyAdapter(getSupportFragmentManager(), c);
                    mPager.setAdapter(mAdapter);
                    mPager.setCurrentItem(initialPosition);
                }


                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                }
            };

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

    public class MyAdapter extends FragmentPagerAdapter {
        Cursor mCursor;
        
        public MyAdapter(FragmentManager fm, Cursor c) {
            super(fm);
            mCursor = c;
        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(cTag, "Creating fragment item " + position);
            mCursor.moveToPosition(position);
            Task task = mPersister.read(mCursor);
            Bundle args = new Bundle();
            args.putInt(TaskViewFragment.INDEX, position);
            args.putInt(TaskViewFragment.COUNT, mCursor.getCount());
            mEncoder.save(args, task);
            return TaskViewFragment.newInstance(args);
        }
    }
    
}
