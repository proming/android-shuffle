package org.dodgybits.shuffle.android.core.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.AndroidException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarFragmentActivity;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarHelper;
import org.dodgybits.shuffle.android.core.fragment.HomeListFragment;
import org.dodgybits.shuffle.android.core.util.Constants;
import org.dodgybits.shuffle.android.list.event.ViewHelpEvent;
import org.dodgybits.shuffle.android.list.event.ViewPreferencesEvent;
import org.dodgybits.shuffle.android.list.listener.NavigationListener;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import roboguice.event.EventManager;

public class HomeActivity extends ActionBarFragmentActivity {
    private static final String TAG = "HomeActivity";

    private static final int WHATS_NEW_DIALOG = 0;

    @Inject
    private HomeListFragment mFragment;

    @Inject
    private EventManager mEventManager;

    @Inject
    private NavigationListener mNavigationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        getActionBarHelper().setDisplayOptions(
                ActionBarHelper.DISPLAY_SHOW_HOME |
                ActionBarHelper.DISPLAY_SHOW_TITLE);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, mFragment);
        ft.commit();

        addVersionToTitle();
        checkLastVersion();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Log.d(TAG, "Bringing up help");
                mEventManager.fire(new ViewHelpEvent());
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

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        if (id == WHATS_NEW_DIALOG) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.whats_new_dialog_title)
                    .setPositiveButton(R.string.ok_button_title, null)
                    .setMessage(R.string.whats_new_dialog_message)
                    .create();
        } else {
            dialog = super.onCreateDialog(id);
        }
        return dialog;
    }
    
    private void addVersionToTitle() {
        String title = getTitle().toString();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            title += " " + info.versionName;
            setTitle(title);
        } catch (AndroidException e) {
            Log.e(TAG, "Failed to add version to title: " + e.getMessage());
        }
    }

    private void checkLastVersion() {
        final int lastVersion = Preferences.getLastVersion(this);
        if (Math.abs(lastVersion) < Math.abs(Constants.cVersion)) {
            // This is a new install or an upgrade.

            // show what's new message
            SharedPreferences.Editor editor = Preferences.getEditor(this);
            editor.putInt(Preferences.LAST_VERSION, Constants.cVersion);
            editor.commit();

            showDialog(WHATS_NEW_DIALOG);
        }
    }

}
