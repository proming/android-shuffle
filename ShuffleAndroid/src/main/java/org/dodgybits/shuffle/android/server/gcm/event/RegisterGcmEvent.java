package org.dodgybits.shuffle.android.server.gcm.event;

import android.content.Context;

public class RegisterGcmEvent {
    private Context context;

    public RegisterGcmEvent(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
