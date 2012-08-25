package org.dodgybits.shuffle.android.preference.activity;

import android.content.Intent;
import android.os.Bundle;
import org.dodgybits.shuffle.android.synchronisation.gae.AccountsActivity;
import roboguice.activity.RoboActivity;

public class PreferencesAppEngineSynchronizationActivity extends RoboActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        startActivity(new Intent(this, AccountsActivity.class));
        finish();
    }    
}
