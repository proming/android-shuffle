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

import static org.dodgybits.shuffle.android.server.sync.SyncSchedulingService.CAUSE_EXTRA;
import static org.dodgybits.shuffle.android.server.sync.SyncSchedulingService.FAILED_UPLOAD_CAUSE;
import static org.dodgybits.shuffle.android.server.sync.SyncSchedulingService.NO_RESPONSE_CAUSE;
import static org.dodgybits.shuffle.android.server.sync.SyncSchedulingService.SOURCE_EXTRA;
import static org.dodgybits.shuffle.android.server.sync.SyncSchedulingService.SYNC_FAILED_SOURCE;

public class GaeSyncService extends RoboIntentService {
    private static final String TAG = "GaeSyncService";

    @Inject
    SyncRequestBuilder requestBuilder;

    @Inject
    SyncResponseProcessor responseProcessor;

    @Inject
    IntegrationSettings integrationSettings;

    @Inject
    AuthTokenRetriever authTokenRetriever;

    private String authToken;

    public GaeSyncService() {
        super("GaeSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Received sync intent");
        performSync();
    }

    private void performSync() {
        authToken = fetchToken();
        if (authToken != null) {
            callService();
        }
    }

    private String fetchToken() {
        String token = Preferences.getSyncAuthToken(this);
        if (token == null) {
            token = authTokenRetriever.retrieveToken();
            Preferences.getEditor(this)
                    .putString(Preferences.SYNC_AUTH_TOKEN, authToken)
                    .commit();
        }
        return token;
    }

    private void callService() {
        AppEngineClient client = new AppEngineClient(integrationSettings.getAppURL(), authToken, this);
        ShuffleProtos.SyncRequest syncRequest = requestBuilder.createRequest();
        byte[] body = syncRequest.toByteArray();
        transmit(body, client, integrationSettings.getSyncURL());
    }

    private void transmit(byte[] body, AppEngineClient client, URL target) {
        Response response = client.post(target, null, body);
        if (response == null) {
            error(client.errorMessage());

            Intent intent = new Intent(this, SyncSchedulingService.class);
            intent.putExtra(SOURCE_EXTRA, SYNC_FAILED_SOURCE);
            intent.putExtra(CAUSE_EXTRA, NO_RESPONSE_CAUSE);
            startService(intent);
            return;
        }

        if (!response.validAuthToken) {
            performSync();
            return;
        }

        if ((response.status / 100) != 2) {
            error("Upload failed: " + response.status);

            Intent intent = new Intent(this, SyncSchedulingService.class);
            intent.putExtra(SOURCE_EXTRA, SYNC_FAILED_SOURCE);
            intent.putExtra(CAUSE_EXTRA, FAILED_UPLOAD_CAUSE);
            startService(intent);

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
