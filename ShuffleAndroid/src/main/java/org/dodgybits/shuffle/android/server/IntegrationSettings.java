package org.dodgybits.shuffle.android.server;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import roboguice.inject.ContextSingleton;

@ContextSingleton
public class IntegrationSettings {

    private String gcmSenderId = "";
    private URL appURL = null;
    private URL syncURL = null;
    private Context context;

    @Inject
    public IntegrationSettings(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("integration.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            gcmSenderId = properties.getProperty("gcm.sender.id");

            String appAddr = properties.getProperty("app.addr");
            if (appAddr != null) {
                appURL = new URL(appAddr);
            }

            String syncAddr = properties.getProperty("sync.addr");
            if (syncAddr != null) {
                syncURL = new URL(syncAddr);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to parse sync URL", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load gcm properties", e);
        }
    }

    public String getGcmSenderId() {
        return gcmSenderId;
    }

    public URL getSyncURL() {
        return syncURL;
    }

    public URL getAppURL() {
        return appURL;
    }
}
