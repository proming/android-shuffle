package org.dodgybits.shuffle.android.list.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.*;
import android.util.Log;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.util.Constants;
import org.dodgybits.shuffle.android.core.util.OSUtils;
import org.dodgybits.shuffle.android.preference.model.ListSettings;

public class ListSettingsEditorActivity extends PreferenceActivity {
    private static final String TAG = "ListSettingsEditor";
    
    private ListSettings mSettings;
    private boolean mPrefsChanged;
    private ListListener mListListener;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSettings = ListSettings.fromIntent(getIntent());
        mListListener = new ListListener();
        
        setupScreen();
    }

    @Override
    public void onResume() {
        super.onResume();

        mPrefsChanged = false;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mPrefsChanged) {
            sendBroadcast(new Intent(ListSettings.LIST_PREFERENCES_UPDATED));
        }
    }

    private void setupScreen() {
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);
        int screenId = getStringId("title_" + mSettings.getPrefix());
        String title = getString(screenId) + " " + getString(R.string.list_settings_title);
        screen.setTitle(title);

        screen.addPreference(createList(
                R.array.list_preferences_active_labels,
                R.string.active_items_title,
                mSettings.getActive(this).name(),
                ListSettings.LIST_FILTER_ACTIVE,
                mSettings.getDefaultActive().name(),
                mSettings.isActiveEnabled()
        ));

        screen.addPreference(createList(
                R.array.list_preferences_pending_labels,
                R.string.pending_items_title,
                mSettings.getPending(this).name(),
                ListSettings.LIST_FILTER_PENDING,
                mSettings.getDefaultPending().name(),
                mSettings.isPendingEnabled()
        ));

        screen.addPreference(createList(
                R.array.list_preferences_completed_labels,
                R.string.completed_items_title,
                mSettings.getCompleted(this).name(),
                ListSettings.LIST_FILTER_COMPLETED,
                mSettings.getDefaultCompleted().name(),
                mSettings.isCompletedEnabled()
        ));

        screen.addPreference(createList(
                R.array.list_preferences_deleted_labels,
                R.string.deleted_items_title,
                mSettings.getDeleted(this).name(),
                ListSettings.LIST_FILTER_DELETED,
                mSettings.getDefaultDeleted().name(),
                mSettings.isDeletedEnabled()
        ));

        screen.addPreference(createQuickAdd());

        setPreferenceScreen(screen);
    }

    private int getStringId(String id) {
        return getResources().getIdentifier(id, Constants.cStringType, Constants.cPackage);
    }

    private ListPreference createList(
            int entries, int title, String value, String keySuffix, Object defaultValue, boolean enabled) {
        ListPreference listPreference = new ListPreference(this);
        listPreference.setEntryValues(R.array.list_preferences_flag_values);
        listPreference.setEntries(entries);
        listPreference.setTitle(title);
        String key = mSettings.getPrefix() + keySuffix;
        listPreference.setKey(key);
        listPreference.setDefaultValue(defaultValue);
        listPreference.setOnPreferenceChangeListener(mListListener);
        listPreference.setEnabled(enabled);

        CharSequence[] entryStrings = listPreference.getEntries();
        int index = listPreference.findIndexOfValue(value);
        if (index > -1) {
            listPreference.setSummary(entryStrings[index]);
        }

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            String message = String.format("Creating list preference key=%s value=%s default=%s title=%s",
                    key, value, defaultValue, title);
            Log.d(TAG, message);
        }

        return listPreference;
    }

    private Preference createQuickAdd() {
        Preference quickAddPref = OSUtils.atLeastICS() ? new SwitchPreference(this) : new CheckBoxPreference(this);
        quickAddPref.setTitle(R.string.quick_add_title);
        quickAddPref.setDefaultValue(mSettings.getDefaultQuickAdd());
        quickAddPref.setKey(mSettings.getPrefix() + ListSettings.LIST_FILTER_QUICK_ADD);
        quickAddPref.setEnabled(mSettings.isQuickAddEnabled());
        quickAddPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mPrefsChanged = true;
                return true;
            }

        });
        return quickAddPref;       
    }
    
    private class ListListener implements Preference.OnPreferenceChangeListener {

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            ListPreference listPreference = (ListPreference)preference;
            int index = listPreference.findIndexOfValue((String)o);
            preference.setSummary(listPreference.getEntries()[index]);
            mPrefsChanged = true;
            return true;
        }

    }
    
}