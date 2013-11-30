package org.dodgybits.shuffle.android.server.gcm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.inject.Inject;
import org.dodgybits.shuffle.android.preference.model.Preferences;

public class GCMInitializer {
//    private static final String TAG = "GCMRegister";
//
//    private Context mContext;
//
//    AsyncTask<Void, Void, Void> mRegisterTask;
//
//    @Inject
//    public GCMInitializer(Context context) {
//        mContext = context;
//
//        if (Preferences.isSyncEnabled(context)) {
//            // TODO wait for auth token and send that too
//            //registerGcm();
//        }
//    }
//
//    private void registerGcm() {
//        Log.i(TAG, "Registering for GCM");
//        // Make sure the device has the proper dependencies.
//        GCMRegistrar.checkDevice(mContext);
//        // Make sure the manifest was properly set - comment out this line
//        // while developing the app, then uncomment it when it's ready.
//        GCMRegistrar.checkManifest(mContext);
//        final String regId = GCMRegistrar.getRegistrationId(mContext);
//        if (regId.equals("")) {
//            // Automatically registers application on startup.
//            GCMRegistrar.register(mContext, CommonUtilities.SENDER_ID);
//        } else {
//            // Device is already registered on GCM, check server.
//            if (GCMRegistrar.isRegisteredOnServer(mContext)) {
//                // Skips registration.
//                Log.d(TAG, "Already registered");
//            } else {
//                // Try to register again, but not in the UI thread.
//                // It's also necessary to cancel the thread onDestroy(),
//                // hence the use of AsyncTask instead of a raw thread.
//                final Context context = mContext;
//                mRegisterTask = new AsyncTask<Void, Void, Void>() {
//
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        boolean registered =
//                                ServerUtilities.register(context, regId);
//                        // At this point all attempts to register with the app
//                        // server failed, so we need to unregister the device
//                        // from GCM - the app will try to register again when
//                        // it is restarted. Note that GCM will send an
//                        // unregistered callback upon completion, but
//                        // GCMIntentService.onUnregistered() will ignore it.
//                        if (!registered) {
//                            GCMRegistrar.unregister(context);
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void result) {
//                        mRegisterTask = null;
//                    }
//
//                };
//                mRegisterTask.execute(null, null, null);
//            }
//        }
//
//    }

}
