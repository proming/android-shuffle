package org.dodgybits.shuffle.android.server.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.inject.Inject;

import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.server.IntegrationSettings;
import org.dodgybits.shuffle.android.server.gcm.event.RegisterGcmEvent;

import java.io.IOException;

import roboguice.event.Observes;
import roboguice.inject.ContextSingleton;

/**
 * Registers for GCM service, if required.
 */
@ContextSingleton
public class GcmRegister {
    static final String TAG = "GCM";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Inject
    private IntegrationSettings integrationSettings;

    GoogleCloudMessaging gcm;

    public void onRegister(@Observes RegisterGcmEvent event) {
        Context context = event.getContext();
        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices(context)) {
            gcm = GoogleCloudMessaging.getInstance(context);
            String registrationId = Preferences.getGcmRegistrationId(context);
            if (registrationId.isEmpty()) {
                registerInBackground(context);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices(Context context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                if (context instanceof Activity) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)context,
                            PLAY_SERVICES_RESOLUTION_REQUEST).show();
                }
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        Log.i(TAG, "Saving regId " + regId);
        SharedPreferences.Editor editor = Preferences.getEditor(context);
        editor.putString(Preferences.GCM_REGISTRATION_ID, regId);
        editor.commit();
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground(final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    String senderId = integrationSettings.getGcmSenderId();
                    String regid = gcm.register(senderId);
                    msg = "Device registered, registration ID=" + regid;

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
                // TODO dispatch event

            }
        }.execute(null, null, null);
    }
}