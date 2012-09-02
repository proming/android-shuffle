package org.dodgybits.shuffle.android.server.sync;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import com.textuality.aerc.Authenticator;

import static org.dodgybits.shuffle.android.server.gcm.CommonUtilities.APP_URI;

public class PrepareSync extends AsyncTask<Void, Void, String> {

    private Activity mActivity;
    private Account mAccount;
    private String mErrorMessage;

    public PrepareSync(Activity activity, Account account) {
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
        if (authToken != null) {
            Intent intent = new Intent(mActivity, GaeSyncService.class);
            intent.putExtra("authtoken", authToken);
            mActivity.startService(intent);
        }

    }

}

