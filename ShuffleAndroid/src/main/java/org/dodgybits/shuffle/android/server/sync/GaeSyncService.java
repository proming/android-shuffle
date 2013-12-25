package org.dodgybits.shuffle.android.server.sync;

import android.content.Intent;
import android.util.Log;

import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.textuality.aerc.AppEngineClient;
import com.textuality.aerc.Response;

import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.android.server.IntegrationSettings;
import org.dodgybits.shuffle.dto.ShuffleProtos;

import java.net.URL;

import roboguice.service.RoboIntentService;

public class GaeSyncService extends RoboIntentService {
    private static final String TAG = "GaeSyncService";

    public static final String SOURCE_EXTRA = "source";

    @Inject
    SyncRequestBuilder requestBuilder;

    @Inject
    SyncResponseProcessor responseProcessor;

    @Inject
    IntegrationSettings integrationSettings;

    private String authToken;

    public GaeSyncService() {
        super("GaeSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Received sync intent");
        authToken = Preferences.getSyncAuthToken(this);

        if (authToken != null) {
            performSync();
        }
    }

    private void performSync() {
        AppEngineClient client = new AppEngineClient(integrationSettings.getAppURL(), authToken, this);
        ShuffleProtos.SyncRequest syncRequest = requestBuilder.createRequest();
        byte[] body = syncRequest.toByteArray();
        transmit(body, client, integrationSettings.getSyncURL());
    }

    private void transmit(byte[] body, AppEngineClient client, URL target) {
        Response response = client.post(target, null, body);
        if (response == null) {
            error(client.errorMessage());
            return;
        }

        if ((response.status / 100) != 2) {
            error("Upload failed: " + response.status);
            // TODO - schedule another attempt
        } else {
            try {
                ShuffleProtos.SyncResponse syncResponse =
                        ShuffleProtos.SyncResponse.parseFrom(response.body);
                responseProcessor.process(syncResponse);
            } catch (InvalidProtocolBufferException e) {
                error("Response parsing failed : " + e.getMessage());
            }
        }
    }

    private void error(String s) {
        Log.e(TAG, s);
    }
}
