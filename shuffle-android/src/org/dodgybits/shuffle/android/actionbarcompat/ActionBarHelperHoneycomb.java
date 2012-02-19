/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.shuffle.android.actionbarcompat;

import android.app.Activity;
import android.content.Context;
import android.view.*;
import org.dodgybits.android.shuffle.R;

/**
 * An extension of {@link ActionBarHelper} that provides Android 3.0-specific functionality for
 * Honeycomb tablets. It thus requires API level 11.
 */
public class ActionBarHelperHoneycomb extends ActionBarHelper {
    private Menu mOptionsMenu;
    private View mRefreshIndeterminateProgressView = null;

    protected ActionBarHelperHoneycomb(Activity activity) {
        super(activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setRefreshActionItemState(boolean refreshing) {
        // On Honeycomb, we can set the state of the refresh button by giving it a custom
        // action view.
        if (mOptionsMenu == null) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
        if (refreshItem != null) {
            if (refreshing) {
                if (mRefreshIndeterminateProgressView == null) {
                    LayoutInflater inflater = (LayoutInflater)
                            getActionBarThemedContext().getSystemService(
                                    Context.LAYOUT_INFLATER_SERVICE);
                    mRefreshIndeterminateProgressView = inflater.inflate(
                            R.layout.actionbar_indeterminate_progress, null);
                }

                refreshItem.setActionView(mRefreshIndeterminateProgressView);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

    @Override
    public void startSupportedActionMode(final ActionMode.Callback callback) {
        mActivity.startActionMode(new CallbackWrapper(callback));
    }

    class CallbackWrapper implements android.view.ActionMode.Callback {

        private ActionMode.Callback mCallback;
        private ActionModeWrapper mActionModeWrapper;

        CallbackWrapper(ActionMode.Callback callback) {
            mCallback = callback;
            mActionModeWrapper = new ActionModeWrapper();
        }

        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            mActionModeWrapper.setDelegate(mode);
            return mCallback.onCreateActionMode(mActionModeWrapper, menu);
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            mActionModeWrapper.setDelegate(mode);
            return mCallback.onPrepareActionMode(mActionModeWrapper, menu);
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            mActionModeWrapper.setDelegate(mode);
            return mCallback.onActionItemClicked(mActionModeWrapper, item);
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {
            mActionModeWrapper.setDelegate(mode);
            mCallback.onDestroyActionMode(mActionModeWrapper);
        }

    }

    class ActionModeWrapper extends ActionMode {

        private android.view.ActionMode mDelegate;

        public android.view.ActionMode getDelegate() {
            return mDelegate;
        }

        public void setDelegate(android.view.ActionMode delegate) {
            mDelegate = delegate;
        }

        @Override
        public void setTag(Object tag) {
            mDelegate.setTag(tag);
        }

        @Override
        public Object getTag() {
            return mDelegate.getTag();
        }

        @Override
        public void setTitle(CharSequence title) {
            mDelegate.setTitle(title);
        }

        @Override
        public void setTitle(int resId) {
            mDelegate.setTitle(resId);
        }

        @Override
        public void setSubtitle(CharSequence subtitle) {
            mDelegate.setSubtitle(subtitle);
        }

        @Override
        public void setSubtitle(int resId) {
            mDelegate.setSubtitle(resId);
        }

        @Override
        public void setCustomView(View view) {
            mDelegate.setCustomView(view);
        }

        @Override
        public void invalidate() {
            mDelegate.invalidate();
        }

        @Override
        public void finish() {
            mDelegate.finish();
        }

        @Override
        public Menu getMenu() {
            return mDelegate.getMenu();
        }

        @Override
        public CharSequence getTitle() {
            return mDelegate.getTitle();
        }

        @Override
        public CharSequence getSubtitle() {
            return mDelegate.getSubtitle();
        }

        @Override
        public View getCustomView() {
            return mDelegate.getCustomView();
        }

        @Override
        public MenuInflater getMenuInflater() {
            return mDelegate.getMenuInflater();
        }

        @Override
        public boolean isUiFocusable() {
            return true;
        }
    }

    /**
     * Returns a {@link android.content.Context} suitable for inflating layouts for the action bar. The
     * implementation for this method in {@link ActionBarHelperICS} asks the action bar for a
     * themed context.
     */
    protected Context getActionBarThemedContext() {
        return mActivity;
    }
}
