package org.dodgybits.shuffle.android.editor.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarFragmentActivity;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarHelper;
import org.dodgybits.shuffle.android.editor.fragment.EditContextFragment;

public class EditContextActivity extends ActionBarFragmentActivity {
    private static final String TAG = "EditContextActivity";

    @Inject
    private EditContextFragment mEditFragment;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d(TAG, "onCreate+");

        setContentView(R.layout.fragment_container);

        getActionBarHelper().setDisplayOptions(ActionBarHelper.DISPLAY_SHOW_CUSTOM,
                ActionBarHelper.DISPLAY_HOME_AS_UP | ActionBarHelper.DISPLAY_SHOW_HOME|
                        ActionBarHelper.DISPLAY_SHOW_TITLE | ActionBarHelper.DISPLAY_SHOW_CUSTOM);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, mEditFragment);
            ft.show(mEditFragment);
            ft.commit();
        }
    }


}
