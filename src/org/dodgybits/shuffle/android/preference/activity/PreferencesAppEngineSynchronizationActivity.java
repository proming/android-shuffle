package org.dodgybits.shuffle.android.preference.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.editor.fragment.AbstractEditFragment;
import org.dodgybits.shuffle.android.preference.fragment.PreferencesAppEngineSynchronizationFragment;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.server.sync.listener.SyncListener;
import roboguice.activity.RoboFragmentActivity;
import roboguice.event.EventManager;
import roboguice.inject.InjectView;

public class PreferencesAppEngineSynchronizationActivity extends RoboFragmentActivity {
    private static final String TAG = "PrefAppEngSyncAct";

    @Inject
    private PreferencesAppEngineSynchronizationFragment mFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_container);

        PreferencesAppEngineSynchronizationFragment currentFragment =
                (PreferencesAppEngineSynchronizationFragment) getSupportFragmentManager().
                findFragmentById(R.id.fragment_container);
        if (currentFragment == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, mFragment);
            ft.show(mFragment);
            ft.commit();
        } else {
            mFragment = currentFragment;
        }
    }

}
