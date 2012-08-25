/*
 * Copyright 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dodgybits.shuffle.android.synchronisation.gae;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.preference.model.Preferences;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for getting the base URL for client-server communication.
 */
public class Util {

    /**
     * Tag for logging.
     */
    private static final String TAG = "Util";

    /*
     * URL suffix for the RequestFactory servlet.
     */
    public static final String RF_METHOD = "/gwtRequest";

    /**
     * An intent name for receiving registration/unregistration status.
     */
    public static final String UPDATE_UI_INTENT = "org.dodgybits.android.shuffle.UPDATE_UI";

    // End shared constants

    /**
     * Cache containing the base URL for a given context.
     */
    private static final Map<Context, String> URL_MAP = new HashMap<Context, String>();

    /**
     * Display a notification containing the given string.
     */
    public static void generateNotification(Context context, String message) {
        int icon = R.drawable.status_icon;
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, message, when);
        notification.setLatestEventInfo(context, "C2DM Example", message,
                PendingIntent.getActivity(context, 0, null, PendingIntent.FLAG_CANCEL_CURRENT));
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        int notificatonID = Preferences.getNotificationId(context);

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificatonID, notification);

        Preferences.incrementNotificationId(context);
    }

    /**
     * Returns the (debug or production) URL associated with the registration
     * service.
     */
    public static String getBaseUrl(Context context) {
        String url = URL_MAP.get(context);
        if (url == null) {
            // if a debug_url raw resource exists, use its contents as the url
            url = getDebugUrl(context);
            // otherwise, use the production url
            if (url == null) {
                url = Setup.PROD_URL;
            }
            URL_MAP.put(context, url);
        }
        return url;
    }
//
//    /**
//     * Creates and returns an initialized {@link RequestFactory} of the given
//     * type.
//     */
//    public static <T extends RequestFactory> T getRequestFactory(Context context,
//            Class<T> factoryClass) {
//        T requestFactory = RequestFactorySource.create(factoryClass);
//
//        String authCookie = Preferences.getGoogleAuthCookie(context);
//
//        String uriString = Util.getBaseUrl(context) + RF_METHOD;
//        URI uri;
//        try {
//            uri = new URI(uriString);
//        } catch (URISyntaxException e) {
//            Log.w(TAG, "Bad URI: " + uriString, e);
//            return null;
//        }
//        requestFactory.initialize(new SimpleEventBus(),
//                new AndroidRequestTransport(uri, authCookie));
//
//        return requestFactory;
//    }

    /**
     * Returns true if we are running against a dev mode appengine instance.
     */
    public static boolean isDebug(Context context) {
        // Although this is a bit roundabout, it has the nice side effect
        // of caching the result.
        return !Setup.PROD_URL.equals(getBaseUrl(context));
    }

    /**
     * Returns a debug url, or null. To set the url, create a file
     * {@code assets/debugging_prefs.properties} with a line of the form
     * 'url=http:/<ip address>:<port>'. A numeric IP address may be required in
     * situations where the device or emulator will not be able to resolve the
     * hostname for the dev mode server.
     */
    private static String getDebugUrl(Context context) {
        BufferedReader reader = null;
        String url = null;
        try {
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("debugging_prefs.properties");
            reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String s = reader.readLine();
                if (s == null) {
                    break;
                }
                if (s.startsWith("url=")) {
                    url = s.substring(4).trim();
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            // O.K., we will use the production server
            return null;
        } catch (Exception e) {
            Log.w(TAG, "Got exception " + e);
            Log.w(TAG, Log.getStackTraceString(e));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.w(TAG, "Got exception " + e);
                    Log.w(TAG, Log.getStackTraceString(e));
                }
            }
        }

        return url;
    }

}
