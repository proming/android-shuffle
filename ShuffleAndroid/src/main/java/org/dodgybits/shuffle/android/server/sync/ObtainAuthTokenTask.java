package org.dodgybits.shuffle.android.server.sync;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import com.textuality.aerc.Authenticator;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import static org.dodgybits.shuffle.android.server.gcm.CommonUtilities.APP_URI;

public class ObtainAuthTokenTask extends AsyncTask<Void, Void, String> {

    private Activity mActivity;
    private Account mAccount;
    private String mErrorMessage;

    public ObtainAuthTokenTask(Activity activity, Account account) {
        mActivity = activity;
        mAccount = account;
    }

    @Override
    protected String doInBackground(Void... params) {
        // ...
        Authenticator authent = Authenticator.appEngineAuthenticator(mActivity, mAccount, APP_URI);
        String authToken = authent.token();
        if (authToken == null) {
            mErrorMessage = authent.errorMessage();
        }

        return authToken;
    }

    @Override
    protected void onPostExecute(String authToken) {
        // ...
        Preferences.getEditor(mActivity)
                .putString(Preferences.SYNC_AUTH_TOKEN, authToken)
                .commit();

        if (authToken == null) {
            Toast.makeText(mActivity.getApplicationContext(), mErrorMessage, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(mActivity, GaeSyncService.class);
            mActivity.startService(intent);
        }
    }

}

