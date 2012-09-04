/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dodgybits.shuffle.android.server.gcm;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

    /**
     * The AppEngine app name, used to construct the production service URL
     * below.
     */
    private static final String APP_NAME = "android-shuffle";

    /**
     * The URL of the production service.
     */
    public static final String APP_ADDR = "https://" + APP_NAME + ".appspot.com";

    /**
     * Local demo service
     */
//    public static final String APP_ADDR = "http://192.168.1.85:8888";


    public static final String SYNC_ADDR = APP_ADDR + "/sync";

    public static final URL APP_URI;
    public static final URL SYNC_URI;

    static {
        try {
            APP_URI = new URL(APP_ADDR);
            SYNC_URI = new URL(SYNC_ADDR);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "andybryant@gmail.com";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

}
