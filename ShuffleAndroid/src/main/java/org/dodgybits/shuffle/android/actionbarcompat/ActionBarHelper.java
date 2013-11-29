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
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.SpinnerAdapter;

/**
 * An abstract class that handles some common action bar-related functionality in the app. This
 * class provides functionality useful for both phones and tablets, and does not require any Android
 * 3.0-specific features, although it uses them if available.
 *
 * Two implementations of this class are {@link ActionBarHelperBase} for a pre-Honeycomb version of
 * the action bar, and {@link ActionBarHelperHoneycomb}, which uses the built-in ActionBar features
 * in Android 3.0 and later.
 */
public abstract class ActionBarHelper {
    /**
     * Standard navigation mode. Consists of either a logo or icon
     * and title text with an optional subtitle. Clicking any of these elements
     * will dispatch onOptionsItemSelected to the host Activity with
     * a MenuItem with item ID android.R.id.home.
     */
    public static final int NAVIGATION_MODE_STANDARD = 0;

    /**
     * List navigation mode. Instead of static title text this mode
     * presents a list menu for navigation within the activity.
     * e.g. this might be presented to the user as a dropdown list.
     */
    public static final int NAVIGATION_MODE_LIST = 1;


    /**
     * Show 'home' elements in this action bar, leaving more space for other
     * navigation elements. This includes logo and icon.
     *
     * @see #setDisplayOptions(int)
     */
    public static final int DISPLAY_SHOW_HOME = 0x2;

    /**
     * Display the 'home' element such that it appears as an 'up' affordance.
     * e.g. show an arrow to the left indicating the action that will be taken.
     *
     * Set this flag if selecting the 'home' button in the action bar to return
     * up by a single level in your UI rather than back to the top level or front page.
     *
     * @see #setDisplayOptions(int)
     */
    public static final int DISPLAY_HOME_AS_UP = 0x4;

    /**
     * Show the activity title and subtitle, if present.
     *
     * @see #setDisplayOptions(int)
     */
    public static final int DISPLAY_SHOW_TITLE = 0x8;

    /**
     * Show the custom view if one has been set.
     * @see #setCustomView(android.view.View)
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     */
    public static final int DISPLAY_SHOW_CUSTOM = 0x10;


    protected Activity mActivity;

    /**
     * Factory method for creating {@link ActionBarHelper} objects for a
     * given activity. Depending on which device the app is running, either a basic helper or
     * Honeycomb-specific helper will be returned.
     */
    public static ActionBarHelper createInstance(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return new ActionBarHelperICS(activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return new ActionBarHelperHoneycomb(activity);
        } else {
            return new ActionBarHelperBase(activity);
        }
    }

    protected ActionBarHelper(Activity activity) {
        mActivity = activity;
    }

    /**
     * Action bar helper code to be run in {@link android.app.Activity#onCreate(android.os.Bundle)}.
     */
    public void onCreate(Bundle savedInstanceState) {
    }

    /**
     * Action bar helper code to be run in {@link android.app.Activity#onPostCreate(android.os.Bundle)}.
     */
    public void onPostCreate(Bundle savedInstanceState) {
    }

    /**
     * Action bar helper code to be run in {@link android.app.Activity#onCreateOptionsMenu(android.view.Menu)}.
     *
     * NOTE: Setting the visibility of menu items in <em>menu</em> is not currently supported.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    public void supportResetOptionsMenu() {
    }

    /**
     * Set the action bar into custom navigation mode, supplying a view
     * for custom navigation.
     *
     * Custom navigation views appear between the application icon and
     * any action buttons and may use any space available there. Common
     * use cases for custom navigation views might include an auto-suggesting
     * address bar for a browser or other navigation mechanisms that do not
     * translate well to provided navigation modes.
     *
     * @param view Custom navigation view to place in the ActionBar.
     */
    public abstract void setCustomView(View view);

    /**
     * Set selected display options. Only the options specified by mask will be changed.
     * To change all display option bits at once, see {@link #setDisplayOptions(int)}.
     *
     * <p>Example: setDisplayOptions(0, DISPLAY_SHOW_HOME) will disable the
     * {@link #DISPLAY_SHOW_HOME} option.
     * setDisplayOptions(DISPLAY_SHOW_HOME, DISPLAY_SHOW_HOME | DISPLAY_USE_LOGO)
     * will enable {@link #DISPLAY_SHOW_HOME} and disable DISPLAY_USE_LOGO.
     *
     * @param options A combination of the bits defined by the DISPLAY_ constants
     *                defined in ActionBar.
     * @param mask A bit mask declaring which display options should be changed.
     */
    public abstract void setDisplayOptions(int options, int mask);

    /**
     * @return The current set of display options.
     */
    public abstract int getDisplayOptions();

    public void setDisplayOptions(int options) {
    }

    /**
     * Action bar helper code to be run in {@link android.app.Activity#onTitleChanged(CharSequence, int)}.
     */
    protected void onTitleChanged(CharSequence title, int color) {
    }

    /**
     * Sets the indeterminate loading state of the item with ID R.id.menu_refresh.
     * (where the item ID was menu_refresh).
     */
    public abstract void setRefreshActionItemState(boolean refreshing);

    /**
     * Returns a {@link android.view.MenuInflater} for use when inflating menus. The implementation of this
     * method in {@link ActionBarHelperBase} returns a wrapped menu inflater that can read
     * action bar metadata from a menu resource pre-Honeycomb.
     */
    public MenuInflater getMenuInflater(MenuInflater superMenuInflater) {
        return superMenuInflater;
    }

    public abstract void startSupportedActionMode(ActionMode.Callback callback);

    /**
     * Returns the current navigation mode. The result will be one of:
     * <ul>
     * <li>{@link #NAVIGATION_MODE_STANDARD}</li>
     * <li>{@link #NAVIGATION_MODE_LIST}</li>
     * </ul>
     *
     * @return The current navigation mode.
     */
    public abstract int getNavigationMode();

    /**
     * Set the current navigation mode.
     *
     * @param mode The new mode to set.
     * @see #NAVIGATION_MODE_STANDARD
     * @see #NAVIGATION_MODE_LIST
     */
    public abstract void setNavigationMode(int mode);

    /**
     * Set the adapter and navigation callback for list navigation mode.
     *
     * The supplied adapter will provide views for the expanded list as well as
     * the currently selected item. (These may be displayed differently.)
     *
     * The supplied OnNavigationListener will alert the application when the user
     * changes the current list selection.
     *
     * @param adapter An adapter that will provide views both to display
     *                the current navigation selection and populate views
     *                within the dropdown navigation menu.
     * @param callback An OnNavigationListener that will receive events when the user
     *                 selects a navigation item.
     */
    public abstract void setListNavigationCallbacks(SpinnerAdapter adapter,
                                                    OnNavigationListener callback);

    /**
     * Set the selected navigation item in list or tabbed navigation modes.
     *
     * @param position Position of the item to select.
     */
    public abstract void setSelectedNavigationItem(int position);

    /**
     * Get the position of the selected navigation item in list or tabbed navigation modes.
     *
     * @return Position of the selected item.
     */
    public abstract int getSelectedNavigationIndex();

    /**
     * Get the number of navigation items present in the current navigation mode.
     *
     * @return Number of navigation items.
     */
    public abstract int getNavigationItemCount();

    /**
     * Listener interface for ActionBar navigation events.
     */
    public interface OnNavigationListener {
        /**
         * This method is called whenever a navigation item in your action bar
         * is selected.
         *
         * @param itemPosition Position of the item clicked.
         * @param itemId ID of the item clicked.
         * @return True if the event was handled, false otherwise.
         */
        public boolean onNavigationItemSelected(int itemPosition, long itemId);
    }


}
