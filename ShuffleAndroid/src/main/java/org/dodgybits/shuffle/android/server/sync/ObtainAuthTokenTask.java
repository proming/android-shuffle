package org.dodgybits.shuffle.android.server.sync;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import com.textuality.aerc.Authenticator;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.server.IntegrationSettings;

public class ObtainAuthTokenTask extends AsyncTask<Void, Void, String> {

    private Activity activity;
    private Account account;
    private String errorMessage;
    private IntegrationSettings integrationSettings;

    public ObtainAuthTokenTask(Activity activity, Account account, IntegrationSettings integrationSettings) {
        this.activity = activity;
        this.account = account;
        this.integrationSettings = integrationSettings;
    }

    @Override
    protected String doInBackground(Void... params) {
        // ...
        Authenticator authent = Authenticator.appEngineAuthenticator(activity, account, integrationSettings.getAppURL());
        String authToken = authent.token();
        if (authToken == null) {
            errorMessage = authent.errorMessage();
        }

        return authToken;
    }

    @Override
    protected void onPostExecute(String authToken) {
        // ...
        Preferences.getEditor(activity)
                .putString(Preferences.SYNC_AUTH_TOKEN, authToken)
                .commit();

        if (authToken == null) {
            Toast.makeText(activity.getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(activity, GaeSyncService.class);
            activity.startService(intent);
        }
    }

}

