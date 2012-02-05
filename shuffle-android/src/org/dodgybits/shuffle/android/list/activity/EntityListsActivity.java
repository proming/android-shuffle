package org.dodgybits.shuffle.android.list.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
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
import org.dodgybits.shuffle.android.list.activity.tasklist.TaskListContext;
import org.dodgybits.shuffle.android.list.activity.tasklist.TaskListFragment;
import org.dodgybits.shuffle.android.list.annotation.DueTasks;
import org.dodgybits.shuffle.android.list.annotation.Inbox;
import org.dodgybits.shuffle.android.list.annotation.Tickler;
import org.dodgybits.shuffle.android.list.annotation.TopTasks;
import roboguice.activity.RoboFragmentActivity;

import java.util.List;

public class EntityListsActivity extends RoboFragmentActivity {
    private static final String cTag = "EntityListsActivity";

    public static final String SELECTED_INDEX = "selectedIndex";
    
    MyAdapter mAdapter;

    ViewPager mPager;

    @Inbox @Inject
    TaskListContext mInboxContext;

    @DueTasks @Inject
    TaskListContext mDueTasksContext;

    @TopTasks @Inject
    TaskListContext mTopTasksContext;

    @Tickler @Inject
    TaskListContext mTicklerContext;

    List<TaskListContext> mContexts;
    ViewPager.OnPageChangeListener mPageChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            ActionBar bar = getActionBar();
            if (bar != null) {
                bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP |
                        ActionBar.DISPLAY_SHOW_HOME |
                        ActionBar.DISPLAY_SHOW_TITLE);
            }
        }

        initContexts();

        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                TaskListContext context = mContexts.get(position);
                setTitle(context.createTitle(EntityListsActivity.this));
            }
        };

        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setOnPageChangeListener(mPageChangeListener);
        mPager.setAdapter(mAdapter);

        int position = 0;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            position = intent.getExtras().getInt(SELECTED_INDEX, 0);
        }

        // TODO remove this once all list screens are present
        position = Math.min(position, mContexts.size() - 1);

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
        }

        return false;
    }

    private void initContexts() {
        mContexts = Lists.newArrayList();

        mContexts.add(mInboxContext);
        mContexts.add(mDueTasksContext);
        mContexts.add(mTopTasksContext);
        mContexts.add(mTicklerContext);
    }

    public class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mContexts.size();
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(cTag, "Creating fragment item " + position);
            return TaskListFragment.newInstance(mContexts.get(position));
        }
    }


}
