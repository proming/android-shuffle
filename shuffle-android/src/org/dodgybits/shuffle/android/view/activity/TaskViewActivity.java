/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.shuffle.android.view.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.TopLevelActivity;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.encoding.TaskEncoder;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.util.OSUtils;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import roboguice.activity.RoboFragmentActivity;
import roboguice.util.Ln;

/**
 * A generic activity for viewing a task.
 */
public class TaskViewActivity extends RoboFragmentActivity {

    @Inject private TaskPersister mPersister;
    @Inject private TaskEncoder mEncoder;

    private Uri mUri;
    private Cursor mCursor;
    private Task mTask;

    @Override
    protected void onCreate(Bundle icicle) {
        Ln.d("onCreate+");
        super.onCreate(icicle);

        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
        setContentView(R.layout.fragment_container);

        if (OSUtils.atLeastHoneycomb())
        {
            ActionBar bar = getActionBar();
            if (bar != null) {
                bar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP |
                        ActionBar.DISPLAY_SHOW_HOME |
                        ActionBar.DISPLAY_SHOW_TITLE);
            }
        }

        mUri = getIntent().getData();
        loadCursor();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCursor.moveToFirst();
        mTask = mPersister.read(mCursor);

        Bundle args = new Bundle();
        mEncoder.save(args, mTask);

        Ln.d("Adding task view fragment to activity");

        TaskViewFragment viewFragment = TaskViewFragment.newInstance(args);
        viewFragment.onVisibilityChange(true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, viewFragment);
        ft.commit();
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

    private void loadCursor() {
        mCursor = managedQuery(mUri, TaskProvider.Tasks.FULL_PROJECTION, null, null, null);
        if (mCursor == null || mCursor.getCount() == 0) {
            // The cursor is empty. This can happen if the event was deleted.
            finish();
        }
    }

}
