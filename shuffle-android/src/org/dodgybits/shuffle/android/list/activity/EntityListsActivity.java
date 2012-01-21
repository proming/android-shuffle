package org.dodgybits.shuffle.android.list.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.list.activity.tasklist.TaskListContext;
import org.dodgybits.shuffle.android.list.activity.tasklist.TaskListFragment;
import org.dodgybits.shuffle.android.list.config.StandardTaskQueries;
import roboguice.activity.RoboFragmentActivity;

public class EntityListsActivity extends RoboFragmentActivity {

    MyAdapter mAdapter;

    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = TaskListFragment.newInstance(new TaskListContext(StandardTaskQueries.cInbox));
                    break;
                case 1:
                    fragment = TaskListFragment.newInstance(new TaskListContext(StandardTaskQueries.cDueNextMonth));
                    break;
                case 2:
                    fragment = TaskListFragment.newInstance(new TaskListContext(StandardTaskQueries.cNextTasks));
                    break;
                case 3:
                    fragment = TaskListFragment.newInstance(new TaskListContext(StandardTaskQueries.cTickler));
                    break;
            }
            return fragment;
        }
    }


}
