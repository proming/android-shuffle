package org.dodgybits.shuffle.android.list.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.list.event.QuickAddEvent;
import roboguice.event.EventManager;

public class QuickAddController {
    private Activity mActivity;
    private boolean mEnabled;

    private View mQuickAdd;
    private EditText mQuickAddValue;
    private ImageView mQuickAddButton;
    private String mEntityName;

    @Inject
    private EventManager mEventManager;
    
    public void init(Activity activity) {
        mActivity = activity;
        findViews();

        mQuickAddValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    commitValue();
                    return true;
                }
                return false;
            }
        });
        mQuickAddValue.setOnFocusChangeListener(new TextView.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    commitValue();
                }
            }
        });

        mQuickAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitValue();
            }
        });
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
        mQuickAdd.setVisibility(mEnabled ? View.VISIBLE : View.GONE);
    }
    
    public void setEntityName(String entityName) {
        mEntityName = entityName;
        String hint = mActivity.getString(R.string.quick_add_hint, entityName);
        mQuickAddValue.setHint(hint);
    }

    private void findViews() {
        mQuickAdd = mActivity.findViewById(R.id.quick_add);
        mQuickAddValue = (EditText)mQuickAdd.findViewById(R.id.quickadd_value);
        mQuickAddButton = (ImageView)mQuickAdd.findViewById(R.id.quickadd_button);
    }
    
    private void commitValue() {
        String description = mQuickAddValue.getText().toString();
        if (!TextUtils.isEmpty(description)) {
            mEventManager.fire(new QuickAddEvent(description));
            mQuickAddValue.setText("");
        }
    }
}
