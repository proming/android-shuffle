package org.dodgybits.shuffle.android.server.sync;

import android.content.Intent;
import android.util.Log;
import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import com.textuality.aerc.AppEngineClient;
import com.textuality.aerc.Response;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import org.dodgybits.shuffle.dto.ShuffleProtos;
import roboguice.service.RoboIntentService;

import java.net.URL;

import static org.dodgybits.shuffle.android.server.gcm.CommonUtilities.APP_URI;
import static org.dodgybits.shuffle.android.server.gcm.CommonUtilities.SYNC_URI;

public class GaeSyncService extends RoboIntentService {
    private static final String TAG = "GaeSyncService";

    @Inject
    SyncRequestBuilder mRequestBuilder;

    @Inject
    SyncResponseProcessor mResponseProcessor;

    private String mAuthToken;

    public GaeSyncService() {
        super("GaeSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Received sync intent");
        mAuthToken = Preferences.getSyncAuthToken(this);

        if (mAuthToken != null) {
            performSync();
        }
    }

    private void performSync() {
        AppEngineClient client = new AppEngineClient(APP_URI, mAuthToken, this);
        ShuffleProtos.SyncRequest syncRequest = mRequestBuilder.createRequest();
        byte[] body = syncRequest.toByteArray();
        transmit(body, client, SYNC_URI);
    }

        private void transmit(byte[] body, AppEngineClient client, URL target) {
        Response response = client.post(target, null, body);
        if (response == null) {
            error(client.errorMessage());
            return;
        }

        if ((response.status / 100) != 2) {
            error("Upload failed: " + response.status);
        } else {
            try {
                ShuffleProtos.SyncResponse syncResponse =
                        ShuffleProtos.SyncResponse.parseFrom(response.body);
                mResponseProcessor.process(syncResponse);
            } catch (InvalidProtocolBufferException e) {
                error("Response parsing failed : " + e.getMessage());
            }
        }
    }

    private void error(String s) {
        Log.e(TAG, s);
    }
}
