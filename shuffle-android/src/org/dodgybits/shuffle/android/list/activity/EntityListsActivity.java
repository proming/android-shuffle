package org.dodgybits.shuffle.android.list.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarFragmentActivity;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarHelper;
import org.dodgybits.shuffle.android.core.activity.HomeActivity;
import org.dodgybits.shuffle.android.list.event.ViewPreferencesEvent;
import org.dodgybits.shuffle.android.list.listener.EntityUpdateListener;
import org.dodgybits.shuffle.android.list.listener.NavigationListener;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.view.context.ContextListFragment;
import org.dodgybits.shuffle.android.list.view.project.ProjectListFragment;
import org.dodgybits.shuffle.android.list.view.task.MultiTaskListContext;
import org.dodgybits.shuffle.android.list.view.task.MultiTaskListFragment;
import org.dodgybits.shuffle.android.list.view.task.TaskListContext;
import org.dodgybits.shuffle.android.list.view.task.TaskListFragment;
import roboguice.event.EventManager;
import roboguice.inject.ContextScopedProvider;

import java.util.List;
import java.util.Map;

public class EntityListsActivity extends ActionBarFragmentActivity {
    private static final String TAG = "EntityListsActivity";

    public static final String QUERY_NAME = "queryName";

    private MyAdapter mAdapter;

    private ViewPager mPager;

    private List<Fragment> mFragments;
    private Map<ListQuery,Integer> mQueryIndex;

    @Inject
    private ContextScopedProvider<TaskListFragment> mTaskListFragmentProvider;

    @Inject
    private ContextScopedProvider<MultiTaskListFragment> mMultiTaskListFragmentProvider;

    @Inject
    private ContextScopedProvider<ContextListFragment> mContextListFragmentProvider;

    @Inject
    private ContextScopedProvider<ProjectListFragment> mProjectListFragmentProvider;

    @Inject 
    private EventManager mEventManager;

    @Inject
    private NavigationListener mNavigationListener;

    @Inject
    private EntityUpdateListener mEntityUpdateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_entity_list_pager);

        getActionBarHelper().setDisplayOptions(ActionBarHelper.DISPLAY_HOME_AS_UP |
                ActionBarHelper.DISPLAY_SHOW_HOME |
                ActionBarHelper.DISPLAY_SHOW_TITLE);

        initFragments();

        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        int position = getRequestedPosition(getIntent());
        mPager.setCurrentItem(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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
        mQueryIndex = Maps.newHashMap();

        addTaskList(ListQuery.inbox);
        addMultiTaskList(Lists.newArrayList(ListQuery.dueToday, ListQuery.dueNextWeek, ListQuery.dueNextMonth));
        addTaskList(ListQuery.nextTasks);

        addFragment(ListQuery.project, mProjectListFragmentProvider.get(this));

        addFragment(ListQuery.context, mContextListFragmentProvider.get(this));

        addTaskList(ListQuery.custom);
        addTaskList(ListQuery.tickler);
    }

    private void addMultiTaskList(List<ListQuery> queries) {
        MultiTaskListContext listContext = MultiTaskListContext.create(queries);
        addFragment(queries, createMultiTaskFragment(listContext));
    }

    private MultiTaskListFragment createMultiTaskFragment(MultiTaskListContext listContext) {
        MultiTaskListFragment fragment = mMultiTaskListFragmentProvider.get(this);
        Bundle args = new Bundle();
        args.putParcelable(TaskListFragment.ARG_LIST_CONTEXT, listContext);
        fragment.setArguments(args);
        return fragment;
    }

    private void addTaskList(ListQuery query) {
        TaskListContext listContext = TaskListContext.create(query);
        addFragment(query, createTaskFragment(listContext));
    }

    private TaskListFragment createTaskFragment(TaskListContext listContext) {
        TaskListFragment fragment = mTaskListFragmentProvider.get(this);
        Bundle args = new Bundle();
        args.putParcelable(TaskListFragment.ARG_LIST_CONTEXT, listContext);
        fragment.setArguments(args);
        return fragment;       
    }
    
    private void addFragment(ListQuery query, Fragment fragment) {
        addFragment(Lists.newArrayList(query), fragment);
    }

    private void addFragment(List<ListQuery> queries, Fragment fragment) {
        mFragments.add(fragment);
        int index = mFragments.size() - 1;
        for (ListQuery query : queries) {
            mQueryIndex.put(query, index);
        }
    }

    private int getRequestedPosition(Intent intent) {
        int position = 0;
        String queryName = intent.getStringExtra(QUERY_NAME);
        if (queryName != null) {
            ListQuery query = ListQuery.valueOf(queryName);
            position = mQueryIndex.get(query);
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
