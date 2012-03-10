package org.dodgybits.shuffle.android.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import roboguice.activity.RoboFragmentActivity;

/**
 * The configuration screen for the DarkWidgetProvider widget.
 */
public class WidgetConfigure extends RoboFragmentActivity {
    private static final String TAG = "WidgetConfigure";
    
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Inject
    private WidgetConfigureListFragment mFragment;

    public WidgetConfigure() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.fragment_container);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, mFragment);
        ft.commit();


        // Find the widget id from the intent.
        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        setTitle(R.string.title_widget_picker);
    }

    public int getAppWidgetId() {
        return mAppWidgetId;
    }
}



