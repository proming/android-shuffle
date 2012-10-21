package org.dodgybits.shuffle.android.preference.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.preference.activity.PreferencesAppEngineSynchronizationActivity;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.server.sync.event.RegisterSyncAccountEvent;
import org.dodgybits.shuffle.android.server.sync.listener.SyncListener;
import roboguice.event.EventManager;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class PreferencesAppEngineSynchronizationFragment extends RoboFragment {
    private static final String TAG = "PrefAppEngSyncAct";

    @InjectView(R.id.enable_sync)
    private CompoundButton mEnableSyncToggleButton;

    @InjectView(R.id.select_account)
    private Button mSelectAccountButton;

    @InjectView(R.id.logged_in_message)
    private TextView mLoggedInTextView;

    @InjectView(R.id.logout)
    private Button mLogoutButton;

    @InjectView(R.id.sync_now)
    private Button mSyncNowButton;

    @InjectView(R.id.last_sync_message)
    private TextView mLastSyncTextView;

    @Inject
    private EventManager mEventManager;

    @Inject
    private SyncListener mSyncListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView+");
        return inflater.inflate(R.layout.preferences_appengine_sync, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupScreen();
    }

    public void onToggleSyncClicked(View view) {
        Preferences.getEditor(getActivity()).putBoolean(Preferences.SYNC_ENABLED, mEnableSyncToggleButton.isChecked()).commit();
        updateViewsOnToggleEnabled();
    }

    public void onSelectAccountClicked(View view) {
        getActivity().showDialog(PreferencesAppEngineSynchronizationActivity.ACCOUNTS_DIALOG);
    }

    public void onLogoutClicked(View view) {
        Preferences.getEditor(getActivity())
                .putString(Preferences.SYNC_ACCOUNT, "")
                .remove(Preferences.SYNC_LAST_SYNC_ID)
                .remove(Preferences.SYNC_LAST_SYNC_GAE_DATE)
                .remove(Preferences.SYNC_LAST_SYNC_LOCAL_DATE)
                .commit();
        updateViewsOnSyncAccountSet();
    }

    public Dialog createAccountsDialog() {
        AccountManager manager = AccountManager.get(getActivity());
        final Account[] accounts = manager.getAccountsByType("com.google");

        final int numAccounts = accounts.length;

        if (numAccounts == 0) {
            return createNoAccountsDialog();
        }

        final CharSequence[] items = new CharSequence[numAccounts];

        String accountName = Preferences.getSyncAccount(getActivity());

        int selectedIndex = -1;
        for (int i=0; i < numAccounts; i++)
        {
            final String name = accounts[i].name;
            if (name.equals(accountName)) {
                selectedIndex = i;
            }
            items[i] = name;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_account_button_title);
        builder.setSingleChoiceItems(items, selectedIndex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Account account = accounts[item];
                String oldAccountName = Preferences.getSyncAccount(getActivity());
                if (!oldAccountName.equals(account.name)) {
                    Log.i(TAG, "Switching from account " + oldAccountName + " to " + account.name);
                    Preferences.getEditor(getActivity())
                            .putString(Preferences.SYNC_ACCOUNT, account.name)
                            .remove(Preferences.SYNC_LAST_SYNC_ID)
                            .remove(Preferences.SYNC_LAST_SYNC_GAE_DATE)
                            .remove(Preferences.SYNC_LAST_SYNC_LOCAL_DATE)
                            .commit();
                    mEventManager.fire(new RegisterSyncAccountEvent(account));
                }
                updateViewsOnSyncAccountSet();
            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }

    private Dialog createNoAccountsDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity())
                        .setMessage(R.string.no_sync_accounts)
                        .setNegativeButton(R.string.cancel_button_title,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                        .setPositiveButton(R.string.ok_button_title,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
                                        getActivity().startActivity(intent);
                                    }
                                });
        return builder.create();
    }


    private void setupScreen() {
        mEnableSyncToggleButton.setChecked(Preferences.isSyncEnabled(getActivity()));
        updateViewsOnToggleEnabled();
        updateViewsOnSyncAccountSet();
    }

    private void updateViewsOnToggleEnabled() {
        final boolean enabled = Preferences.isSyncEnabled(getActivity());

        mSelectAccountButton.setEnabled(enabled);
        mLogoutButton.setEnabled(enabled);
        mSyncNowButton.setEnabled(enabled);
    }

    private void updateViewsOnSyncAccountSet() {
        String syncAccount = Preferences.getSyncAccount(getActivity());
        final boolean accountSet = syncAccount.length() > 0;
        mSelectAccountButton.setVisibility(accountSet ? View.GONE : View.VISIBLE);
        mLoggedInTextView.setVisibility(accountSet ? View.VISIBLE : View.GONE);
        mLoggedInTextView.setText(getString(R.string.sync_selected_account, syncAccount));
        mLogoutButton.setVisibility(accountSet ? View.VISIBLE : View.GONE);
        mSyncNowButton.setVisibility(accountSet ? View.VISIBLE : View.GONE);
        mLastSyncTextView.setVisibility(accountSet ? View.VISIBLE : View.GONE);
        long lastSyncDate = Preferences.getLastSyncLocalDate(getActivity());
        if (lastSyncDate == 0L) {
            mLastSyncTextView.setText(R.string.no_previous_sync);
        } else {
            CharSequence syncDate = DateUtils.getRelativeTimeSpanString(getActivity(), lastSyncDate, false);
            mLastSyncTextView.setText(getString(R.string.last_sync_title, syncDate));
        }

    }

}
