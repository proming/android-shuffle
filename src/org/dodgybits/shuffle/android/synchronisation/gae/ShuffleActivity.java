/*
 * Copyright 2010 Google Inc.
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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.preference.model.Preferences;

/**
 * Main activity - requests "Hello, World" messages from the server and provides
 * a menu item to invoke the accounts activity.
 */
public class ShuffleActivity extends Activity {
    /**
     * Tag for logging.
     */
    private static final String TAG = "ShuffleActivity";

    /**
     * The current context.
     */
    private Context mContext = this;

    /**
     * A {@link BroadcastReceiver} to receive the response from a register or
     * unregister request, and to update the UI.
     */
    private final BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(DeviceRegistrar.STATUS_EXTRA,
                    DeviceRegistrar.ERROR_STATUS);
            String message = null;
            if (status == DeviceRegistrar.REGISTERED_STATUS) {
                message = getResources().getString(R.string.registration_succeeded);
            } else if (status == DeviceRegistrar.UNREGISTERED_STATUS) {
                message = getResources().getString(R.string.unregistration_succeeded);
            } else {
                message = getResources().getString(R.string.registration_error);
            }

            // Display a notification
            String accountName = Preferences.getGoogleAccountName(mContext);
            Util.generateNotification(mContext, String.format(message, accountName));
        }
    };

    /**
     * Begins the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setScreenContent(R.layout.hello_world);

        // Register a receiver to provide register/unregister notifications
        registerReceiver(mUpdateUIReceiver, new IntentFilter(Util.UPDATE_UI_INTENT));
    }

    /**
     * Shuts down the activity.
     */
    @Override
    public void onDestroy() {
        unregisterReceiver(mUpdateUIReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // Invoke the Register activity
        menu.getItem(0).setIntent(new Intent(this, AccountsActivity.class));
        return true;
    }

    // Manage UI Screens

    private void setHelloWorldScreenContent() {
        setContentView(R.layout.hello_world);
//
//        final TextView helloWorld = (TextView) findViewById(R.id.hello_world);
//        final Button sayHelloButton = (Button) findViewById(R.id.say_hello);
//        sayHelloButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                sayHelloButton.setEnabled(false);
//                helloWorld.setText(R.string.contacting_server);
//
//                // Use an AsyncTask to avoid blocking the UI thread
//                new AsyncTask<Void, Void, String>() {
//                    private String message;
//
//                    @Override
//                    protected String doInBackground(Void... arg0) {
//                        ShuffleRequestFactory requestFactory = Util.getRequestFactory(mContext,
//                        		ShuffleRequestFactory.class);
//                        final HelloWorldRequest request = requestFactory.helloWorldRequest();
//                        String accountName = Preferences.getGoogleAccountName(mContext);
//                        Log.i(TAG, "Sending request to server for account " + accountName);
//                        request.getMessage().fire(new Receiver<String>() {
//                            @Override
//                            public void onFailure(ServerFailure error) {
//                                message = "Failure: " + error.getMessage();
//                            }
//
//                            @Override
//                            public void onSuccess(String result) {
//                                message = result;
//                            }
//                        });
//                        return message;
//                    }
//
//                    @Override
//                    protected void onPostExecute(String result) {
//                        helloWorld.setText(result);
//                        sayHelloButton.setEnabled(true);
//                    }
//                }.execute();
//            }
//        });
    }

    /**
     * Sets the screen content based on the screen id.
     */
    private void setScreenContent(int screenId) {
        setContentView(screenId);
        switch (screenId) {
            case R.layout.hello_world:
                setHelloWorldScreenContent();
                break;
        }
    }
}
