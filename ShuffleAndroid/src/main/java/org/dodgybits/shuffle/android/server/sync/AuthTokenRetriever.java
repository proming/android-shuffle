package org.dodgybits.shuffle.android.server.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.inject.Inject;
import com.textuality.aerc.Authenticator;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.server.IntegrationSettings;

import roboguice.inject.ContextSingleton;

import static org.dodgybits.shuffle.android.preference.fragment.PreferencesAppEngineSynchronizationFragment.GOOGLE_ACCOUNT;

@ContextSingleton
public class AuthTokenRetriever {
    public static final String TAG = "AuthTokenRetriever";

    private Context context;
    private IntegrationSettings integrationSettings;

    @Inject
    public AuthTokenRetriever(Context context, IntegrationSettings integrationSettings) {
        this.context = context;
        this.integrationSettings = integrationSettings;
    }

    public String retrieveToken() {
        String authToken = null;
        Account account = fetchAccount();
        if (account == null) {
           Log.e(TAG, "Could not determine Google account for sync");
        } else {
            Authenticator authent = Authenticator.appEngineAuthenticator(context, account, integrationSettings.getAppURL());
            authToken = authent.token();
            if (authToken == null) {
                Log.e(TAG, authent.errorMessage());
            }
        }

        return authToken;
    }

    private Account fetchAccount() {
        String accountName = Preferences.getSyncAccount(context);
        Account account = null;
        AccountManager manager = AccountManager.get(context);
        final Account[] accounts = manager.getAccountsByType(GOOGLE_ACCOUNT);
        final int numAccounts = accounts.length;
        for (int i=0; i < numAccounts; i++)
        {
            final String name = accounts[i].name;
            if (name.equals(accountName)) {
                account = accounts[i];
                break;
            }
        }
        return account;
    }

}

