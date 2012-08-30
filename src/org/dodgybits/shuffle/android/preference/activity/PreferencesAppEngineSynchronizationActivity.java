package org.dodgybits.shuffle.android.preference.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.preference.*;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.util.OSUtils;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.server.sync.event.RegisterSyncAccountEvent;
import org.dodgybits.shuffle.android.server.sync.listener.SyncListener;
import roboguice.activity.RoboPreferenceActivity;
import roboguice.event.EventManager;

public class PreferencesAppEngineSynchronizationActivity extends RoboPreferenceActivity {
    private static final String TAG = "PrefAppEngSyncAct";

    private boolean mPrefsChanged;
    private String mOldAccountName;
    private Account mNewAccount;
    private boolean mOldActive;
    private boolean mNewActive;
    private Account[] mAccounts;

    @Inject
    private EventManager mEventManager;

    @Inject
    private SyncListener mSyncListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AccountManager manager = AccountManager.get(this);
        mAccounts = manager.getAccountsByType("com.google");

        setupScreen();
    }

    @Override
    public void onResume() {
        super.onResume();

        mPrefsChanged = false;
        mOldAccountName = Preferences.getSyncAccount(this);
        mOldActive = Preferences.isSyncEnabled(this);

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mPrefsChanged) {
            mNewActive = Preferences.isSyncEnabled(this);
            String accountName = Preferences.getSyncAccount(this);

            if (accountName != null && !accountName.equals(mOldAccountName)) {
                mNewAccount = null;
                for (Account account : mAccounts) {
                    if (account.name.equals(accountName)) {
                        mNewAccount = account;
                        break;
                    }
                }

                mEventManager.fire(new RegisterSyncAccountEvent(mNewAccount));
            }

//            Intent intent = new Intent(ListSettings.LIST_PREFERENCES_UPDATED);
//            intent.putExtra(LIST_QUERY_EXTRA, mListQuery.name());
//            sendBroadcast(intent);
        }
    }

    private void setupScreen() {
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
        screen.setTitle(R.string.title_gae_sync_preferences);

        final Preference accountSelector = createAccountSelector();
        accountSelector.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mPrefsChanged = true;
                return true;
            }

        });


        final TwoStatePreference syncToggle = createSyncToggle();
        syncToggle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mPrefsChanged = true;
                accountSelector.setEnabled(!syncToggle.isChecked());
                return true;
            }

        });

        screen.addPreference(syncToggle);
        screen.addPreference(accountSelector);

        setPreferenceScreen(screen);
    }

    private TwoStatePreference createSyncToggle() {
        TwoStatePreference syncTogglePref = OSUtils.atLeastICS() ? new SwitchPreference(this) : new CheckBoxPreference(this);
        syncTogglePref.setTitle(R.string.enable_sync_button_title);
        syncTogglePref.setDefaultValue(false);
        syncTogglePref.setKey(Preferences.SYNC_ENABLED);
        syncTogglePref.setEnabled(Preferences.isSyncEnabled(this));
        return syncTogglePref;
    }

    private Preference createAccountSelector() {
        final int numAccounts = mAccounts.length;
        CharSequence[] accountNames = new CharSequence[numAccounts + 1];
        CharSequence[] accountValues = new CharSequence[numAccounts + 1];

        accountNames[0] = getString(R.string.none_empty);
        accountValues[0] = "";

        int selectedIndex = 0;
        String accountName = Preferences.getSyncAccount(this);

        for (int i=0; i < numAccounts; i++)
        {
            final String name = mAccounts[i].name;
            if (name.equals(accountName)) {
                selectedIndex = i+1;
            }
            accountNames[i+1] = accountValues[i+1] = name;

        }

        ListPreference listPreference = new ListPreference(this);
        listPreference.setKey(Preferences.SYNC_ACCOUNT);
        listPreference.setEntries(accountNames);
        listPreference.setEntryValues(accountValues);
        listPreference.setTitle(R.string.sync_selected_account);
        listPreference.setSummary(accountName);
        listPreference.setValueIndex(selectedIndex);
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mPrefsChanged = true;
                return true;
            }
        });

        return listPreference;
    }



}
