package org.dodgybits.shuffle.android.list.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarFragmentActivity;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.persistence.ContextPersister;
import org.dodgybits.shuffle.android.core.util.OSUtils;
import org.dodgybits.shuffle.android.list.content.ContextCursorLoader;
import org.dodgybits.shuffle.android.list.event.ViewPreferencesEvent;
import org.dodgybits.shuffle.android.list.listener.NavigationListener;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.view.task.TaskListContext;
import org.dodgybits.shuffle.android.list.view.task.TaskListFragment;
import roboguice.event.EventManager;
import roboguice.inject.ContextScopedProvider;

public class ContextTaskListsActivity extends ActionBarFragmentActivity {
    public static final String TAG = "ContextTaskListsActivity";
    public static final String INITIAL_POSITION = "initialPosition";

    private static final int LOADER_ID_CONTEXT_LIST_LOADER = 1;

    private MyAdapter mAdapter;

    private ViewPager mPager;

    @Inject
    private ContextPersister mPersister;

    @Inject
    ContextScopedProvider<TaskListFragment> mTaskListFragmentProvider;

    @Inject
    private EventManager mEventManager;

    @Inject
    private NavigationListener mNavigationListener;

    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(TAG, "onCreate+");
        super.onCreate(icicle);
        setContentView(R.layout.fragment_pager);

        if (OSUtils.atLeastHoneycomb())
        {
            ActionBar bar = getActionBar();
            if (bar != null) {
                bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP |
                        ActionBar.DISPLAY_SHOW_HOME |
                        ActionBar.DISPLAY_SHOW_TITLE);
            }
        }

        mPager = (ViewPager)findViewById(R.id.pager);

        startLoading();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, EntityListsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(EntityListsActivity.QUERY_NAME, ListQuery.context.name());
                startActivity(intent);
                return true;
            case R.id.action_preferences:
                Log.d(TAG, "Bringing up preferences");
                mEventManager.fire(new ViewPreferencesEvent());
                return true;
            case R.id.action_search:
                Log.d(TAG, "Bringing up search");
                onSearchRequested();
                return true;
        }

        return false;
    }

    private void startLoading() {
        Log.d(TAG, "Creating context list cursor");
        final LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(LOADER_ID_CONTEXT_LIST_LOADER, getIntent().getExtras(), LOADER_CALLBACKS);
    }


    /**
     * Loader callbacks for message list.
     */
    private final LoaderManager.LoaderCallbacks<Cursor> LOADER_CALLBACKS =
            new LoaderManager.LoaderCallbacks<Cursor>() {

                int mInitialPosition;

                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    mInitialPosition = args.getInt(INITIAL_POSITION, 0);
                    return new ContextCursorLoader(ContextTaskListsActivity.this);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
                    mAdapter = new MyAdapter(getSupportFragmentManager(), c);
                    mPager.setAdapter(mAdapter);
                    mPager.setCurrentItem(mInitialPosition);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                }
            };

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
            mCursor.moveToPosition(position);
            Id contextId = mPersister.read(mCursor).getLocalId();
            TaskListContext listContext = TaskListContext.createForContext(contextId);
            TaskListFragment fragment = mTaskListFragmentProvider.get(ContextTaskListsActivity.this);
            Bundle args = new Bundle();
            args.putParcelable(TaskListFragment.ARG_LIST_CONTEXT, listContext);
            fragment.setArguments(args);
            return fragment;
        }
    }
}
