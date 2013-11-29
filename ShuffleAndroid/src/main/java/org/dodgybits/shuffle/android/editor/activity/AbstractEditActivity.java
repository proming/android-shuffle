package org.dodgybits.shuffle.android.editor.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarFragmentActivity;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarHelper;
import org.dodgybits.shuffle.android.editor.fragment.AbstractEditFragment;

public abstract class AbstractEditActivity extends ActionBarFragmentActivity {
    private static final String TAG = "EditProjectActivity";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d(TAG, "onCreate+");

        setContentView(R.layout.fragment_container);

        getActionBarHelper().setDisplayOptions(ActionBarHelper.DISPLAY_SHOW_CUSTOM,
                ActionBarHelper.DISPLAY_HOME_AS_UP | ActionBarHelper.DISPLAY_SHOW_HOME|
                        ActionBarHelper.DISPLAY_SHOW_TITLE | ActionBarHelper.DISPLAY_SHOW_CUSTOM);

        AbstractEditFragment currentFragment = (AbstractEditFragment) getSupportFragmentManager().
                findFragmentById(R.id.fragment_container);
        if (currentFragment == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, getFragment());
            ft.show(getFragment());
            ft.commit();
        } else {
            setFragment(currentFragment);
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragment() != null) {
            getFragment().save();
        }

        super.onBackPressed();
    }

    @Override
    public void finish() {
        if (getFragment() != null && getFragment().getNextIntent() != null) {
            startActivity(getFragment().getNextIntent());
        }
        super.finish();
    }


    protected abstract AbstractEditFragment getFragment();
    protected abstract void setFragment(AbstractEditFragment fragment);


}
