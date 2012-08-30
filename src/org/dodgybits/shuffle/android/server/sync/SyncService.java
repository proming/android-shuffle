package org.dodgybits.shuffle.android.server.sync;

import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import com.google.inject.Inject;
import com.textuality.aerc.AppEngineClient;
import com.textuality.aerc.Response;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.model.protocol.ContextProtocolTranslator;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.dto.ShuffleProtos;
import roboguice.service.RoboIntentService;

import java.net.URL;

import static org.dodgybits.shuffle.android.server.gcm.CommonUtilities.APP_URI;
import static org.dodgybits.shuffle.android.server.gcm.CommonUtilities.SYNC_URI;

public class SyncService extends RoboIntentService {
    private static final String TAG = "SyncService";

    @Inject
    EntityPersister<Context> mContextPersister;

    private String mAuthToken;

    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mAuthToken = intent.getStringExtra("authtoken");

        performSync();
    }

    private void performSync() {
        AppEngineClient client = new AppEngineClient(APP_URI, mAuthToken, this);
        byte[] body = createSyncRequestBody();
        transmit(body, client, SYNC_URI);
    }

    private byte[] createSyncRequestBody() {
        ShuffleProtos.Catalogue.Builder builder = ShuffleProtos.Catalogue.newBuilder();

        writeContexts(builder);

        return builder.build().toByteArray();
    }


    private void writeContexts(ShuffleProtos.Catalogue.Builder builder)
    {
        Log.d(TAG, "Writing contexts");
        Cursor cursor = getContentResolver().query(
                ContextProvider.Contexts.CONTENT_URI, ContextProvider.Contexts.FULL_PROJECTION,
                null, null, null);
        ContextProtocolTranslator translator = new ContextProtocolTranslator();
        while (cursor.moveToNext()) {
            Context context = mContextPersister.read(cursor);
            builder.addContext(translator.toMessage(context));
        }
        cursor.close();
    }


    private void transmit(byte[] body, AppEngineClient client, URL target) {
        Response response = client.post(target, null, body);
        if (response == null) {
            error(client.errorMessage());
        }

        if ((response.status / 100) != 2) {
            error("Upload failed: " + response.status);
        }

        Log.d(TAG, "Received "  + new String(response.body));
    }

    private void error(String s) {
        Log.e(TAG, s);
    }
}
