package org.dodgybits.shuffle.android.list.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.TopLevelActivity;
import org.dodgybits.shuffle.android.core.util.OSUtils;
import org.dodgybits.shuffle.android.list.event.ViewPreferencesEvent;
import org.dodgybits.shuffle.android.list.listener.NavigationListener;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.model.ListTitles;
import org.dodgybits.shuffle.android.list.view.Titled;
import org.dodgybits.shuffle.android.list.view.context.ContextListFragment;
import org.dodgybits.shuffle.android.list.view.project.ProjectListFragment;
import org.dodgybits.shuffle.android.list.view.task.TaskListContext;
import org.dodgybits.shuffle.android.list.view.task.TaskListFragment;
import roboguice.activity.RoboFragmentActivity;
import roboguice.event.EventManager;
import roboguice.inject.ContextScopedProvider;

import java.util.List;

public class EntityListsActivity extends RoboFragmentActivity {
    private static final String TAG = "EntityListsActivity";

    public static final String QUERY_NAME = "queryName";

    private MyAdapter mAdapter;

    private ViewPager mPager;

    private List<Fragment> mFragments;
    private List<ListQuery> mQueries;

    private ViewPager.OnPageChangeListener mPageChangeListener;

    @Inject
    private ContextScopedProvider<TaskListFragment> mTaskListFragmentProvider;

    @Inject
    private ContextScopedProvider<ContextListFragment> mContextListFragmentProvider;

    @Inject
    private ContextScopedProvider<ProjectListFragment> mProjectListFragmentProvider;

    @Inject 
    private EventManager mEventManager;

    @Inject
    private NavigationListener mNavigationListener;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        initFragments();

        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Fragment fragment = mFragments.get(position);
                if (fragment instanceof Titled) {
                    setTitle(((Titled)fragment).getTitle(EntityListsActivity.this));
                } else {
                    ListQuery query = mQueries.get(position);
                    setTitle(ListTitles.getTitleId(query));
                }
            }
        };

        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setOnPageChangeListener(mPageChangeListener);
        mPager.setAdapter(mAdapter);

        int position = getRequestedPosition(getIntent());
        mPager.setCurrentItem(position);

        // pager doesn't notify on initial page selection (if it's 0)
        mPageChangeListener.onPageSelected(mPager.getCurrentItem());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, TopLevelActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private void initFragments() {
        mFragments = Lists.newArrayList();
        mQueries = Lists.newArrayList();

        addTaskList(ListQuery.inbox);
        addTaskList(ListQuery.dueToday);
        addTaskList(ListQuery.nextTasks);

        addFragment(ListQuery.project, mProjectListFragmentProvider.get(this));

        addFragment(ListQuery.context, mContextListFragmentProvider.get(this));

        addTaskList(ListQuery.custom);
        addTaskList(ListQuery.tickler);
    }

    private void addTaskList(ListQuery query) {
        TaskListContext listContext = TaskListContext.create(query);
        TaskListFragment fragment = mTaskListFragmentProvider.get(this);
        Bundle args = new Bundle();
        args.putParcelable(TaskListFragment.ARG_LIST_CONTEXT, listContext);
        fragment.setArguments(args);
        addFragment(query, fragment);
    }

    private void addFragment(ListQuery query, Fragment fragment) {
        mFragments.add(fragment);
        mQueries.add(query);
    }

    private int getRequestedPosition(Intent intent) {
        int position = 0;
        String queryName = intent.getStringExtra(QUERY_NAME);
        if (queryName != null) {
            ListQuery query = ListQuery.valueOf(queryName);
            position = mQueries.indexOf(query);
            if (position == -1) {
                Log.e(TAG, "Couldn't find page of list " + queryName);
                position = 0;
            }
        }

        return position;
    }

    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }


}
