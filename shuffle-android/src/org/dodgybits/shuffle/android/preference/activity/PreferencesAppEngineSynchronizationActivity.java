package org.dodgybits.shuffle.android.preference.activity;

import org.dodgybits.android.shuffle.AccountsActivity;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledActivity;

import android.content.Intent;
import android.os.Bundle;

public class PreferencesAppEngineSynchronizationActivity extends FlurryEnabledActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        startActivity(new Intent(this, AccountsActivity.class));
        finish();
    }    
}
