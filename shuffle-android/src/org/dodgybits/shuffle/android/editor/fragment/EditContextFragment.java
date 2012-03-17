package org.dodgybits.shuffle.android.editor.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarFragmentActivity;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.encoding.EntityEncoder;
import org.dodgybits.shuffle.android.core.model.persistence.EntityPersister;
import org.dodgybits.shuffle.android.core.util.TextColours;
import org.dodgybits.shuffle.android.core.view.ContextIcon;
import org.dodgybits.shuffle.android.core.view.DrawableUtils;
import org.dodgybits.shuffle.android.editor.activity.ColourPickerActivity;
import org.dodgybits.shuffle.android.editor.activity.IconPickerActivity;
import org.dodgybits.shuffle.android.list.view.context.ContextListItem;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import roboguice.fragment.RoboFragment;

public class EditContextFragment extends RoboFragment 
        implements View.OnClickListener, View.OnFocusChangeListener, TextWatcher {
    private static final String TAG = "EditContextFragment";

    private static final int COLOUR_PICKER = 0;
    private static final int ICON_PICKER = 1;

    private int mColourIndex;
    private ContextIcon mIcon;

    private EditText mNameWidget;
    private TextView mColourWidget;
    private ImageView mIconWidget;
    private TextView mIconNoneWidget;
    private ImageButton mClearIconButton;
    private View mDeletedDivider;
    private View mDeletedEntry;
    private CheckBox mDeletedCheckBox;
    private View mActiveEntry;
    private CheckBox mActiveCheckBox;
    private ContextListItem mContextPreview;
    
    @Inject
    private EntityPersister<Context> mPersister;
    @Inject
    private EntityEncoder<Context> mEncoder;

    private Uri mUri;
    private boolean mIsNewEntity;
    private Cursor mCursor;
    private Context mOriginalItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView+");

        View view;
        view = inflater.inflate(R.layout.context_editor, null);

        View actionBarButtons = inflater.inflate(R.layout.edit_custom_actionbar,
                new LinearLayout(getActivity()), false);
        View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
        cancelActionView.setOnClickListener(this);
        View doneActionView = actionBarButtons.findViewById(R.id.action_done);
        doneActionView.setOnClickListener(this);

        getActionBarFragmentActivity().getActionBarHelper().setCustomView(actionBarButtons);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated+");
        
        mUri = getActivity().getIntent().getData();
        mIsNewEntity = Intent.ACTION_INSERT.equals(getActivity().getIntent().getAction());
        loadCursor();
        findViewsAndAddListeners();
        if (mIsNewEntity) {
            resetUI();
        } else {
            mOriginalItem = mPersister.read(mCursor);
            updateUIFromItem(mOriginalItem);
        }

        if (savedInstanceState != null) {
            // Fragment doesn't have this method.  Call it manually.
            restoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getActionBarFragmentActivity().getActionBarHelper().setCustomView(null);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Got click on " + v.getId());
        switch (v.getId()) {
            case R.id.colour_entry: {
                // Launch activity to pick colour
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ColourPickerActivity.TYPE);
                startActivityForResult(intent, COLOUR_PICKER);
                break;
            }

            case R.id.icon_entry: {
                // Launch activity to pick icon
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(IconPickerActivity.TYPE);
                startActivityForResult(intent, ICON_PICKER);
                break;
            }

            case R.id.icon_clear_button: {
                mIcon = ContextIcon.NONE;
                displayIcon();
                updatePreview();
                break;
            }

            case R.id.active_entry: {
                mActiveCheckBox.toggle();
                updatePreview();
                break;
            }

            case R.id.deleted_entry: {
                mDeletedCheckBox.toggle();
                updatePreview();
                break;
            }

            case R.id.action_done:
                doSaveAction();
                break;

            case R.id.action_cancel:
                doRevertAction();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Context item = createItemFromUI(false);
        mEncoder.save(outState, item);
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        Context item = mEncoder.restore(savedInstanceState);
        updateUIFromItem(item);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // Because we're emulating a ListView, we need to setSelected() for
        // views as they are focused.
        v.setSelected(hasFocus);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Got resultCode " + resultCode + " with data " + data);
        switch (requestCode) {
            case COLOUR_PICKER:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        mColourIndex = Integer.parseInt(data.getStringExtra("colour"));
                        displayColour();
                        updatePreview();
                    }
                }
                break;
            case ICON_PICKER:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        String iconName = data.getStringExtra("iconName");
                        mIcon = ContextIcon.createIcon(iconName, getResources());
                        displayIcon();
                        updatePreview();
                    }
                }
                break;
            default:
                Log.e(TAG, "Unknown requestCode: " + requestCode);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        updatePreview();
    }

    private void findViewsAndAddListeners() {
        mNameWidget = (EditText) getView().findViewById(R.id.name);
        mColourWidget = (TextView) getView().findViewById(R.id.colour_display);
        mIconWidget = (ImageView) getView().findViewById(R.id.icon_display);
        mIconNoneWidget = (TextView) getView().findViewById(R.id.icon_none);
        mClearIconButton = (ImageButton) getView().findViewById(R.id.icon_clear_button);
        mDeletedEntry = getView().findViewById(R.id.deleted_entry);
        mDeletedDivider = getView().findViewById(R.id.deleted_divider);
        mActiveEntry = getView().findViewById(R.id.active_entry);
        mActiveCheckBox = (CheckBox) getView().findViewById(R.id.active_entry_checkbox);
        mContextPreview = (ContextListItem) getView().findViewById(R.id.context_preview);

        mNameWidget.addTextChangedListener(this);

        mColourIndex = -1;
        mIcon = ContextIcon.NONE;

        View colourEntry = getView().findViewById(R.id.colour_entry);
        colourEntry.setOnClickListener(this);
        colourEntry.setOnFocusChangeListener(this);

        View iconEntry = getView().findViewById(R.id.icon_entry);
        iconEntry.setOnClickListener(this);
        iconEntry.setOnFocusChangeListener(this);

        mClearIconButton.setOnClickListener(this);
        mClearIconButton.setOnFocusChangeListener(this);

        mActiveEntry.setOnClickListener(this);
        mActiveEntry.setOnFocusChangeListener(this);

        mDeletedEntry.setOnClickListener(this);
        mDeletedEntry.setOnFocusChangeListener(this);
        mDeletedCheckBox = (CheckBox) mDeletedEntry.findViewById(R.id.deleted_entry_checkbox);
    }

    public void doSaveAction() {
        // Save or create the contact if needed
        Uri result = null;
        if (mIsNewEntity) {
            result = create();
        } else {
            result = save();
        }

        if (result == null) {
            getActivity().setResult(Activity.RESULT_CANCELED);
        } else {
            getActivity().setResult(Activity.RESULT_OK, new Intent().setData(result));
        }
        getActivity().finish();
    }

    /**
     * Take care of canceling work on a item. Deletes the item if we had created
     * it, otherwise reverts to the original text.
     */
    protected void doRevertAction() {
        if (mCursor != null) {
            if (!mIsNewEntity) {
                // Put the original item back into the database
                mCursor.close();
                mCursor = null;
                mPersister.update(mOriginalItem);
            }
        }
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();
    }


    protected Uri create() {
        Uri uri = null;
        if (isValid()) {
            Context item = createItemFromUI(true);
            uri = mPersister.insert(item);
            showSaveToast();
        }
        return uri;
    }

    protected Uri save() {
        Uri uri = null;
        if (isValid()) {
            Context item = createItemFromUI(true);
            mPersister.update(item);
            showSaveToast();
            uri = mUri;
        }
        return uri;
    }

    protected final void showSaveToast() {
        String text;
        if (mIsNewEntity) {
            text = getResources().getString(R.string.itemCreatedToast,
                    getString(R.string.context_name));
        } else {
            text = getResources().getString(R.string.itemSavedToast,
                    getString(R.string.context_name));
        }
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    private void loadCursor() {
        if (mUri != null && !mIsNewEntity) {
            mCursor = getActivity().managedQuery(mUri, ContextProvider.Contexts.FULL_PROJECTION, null, null, null);
            if (mCursor == null || mCursor.getCount() == 0) {
                // The cursor is empty. This can happen if the event was deleted.
                getActivity().finish();
            }
            mCursor.moveToFirst();
        }
    }

    private boolean isValid() {
        String name = mNameWidget.getText().toString();
        return !TextUtils.isEmpty(name);
    }


    private Context createItemFromUI(boolean commitValues) {
        Context.Builder builder = Context.newBuilder();
        if (mOriginalItem != null) {
            builder.mergeFrom(mOriginalItem);
        }

        builder.setName(mNameWidget.getText().toString());
        builder.setModifiedDate(System.currentTimeMillis());
        builder.setColourIndex(mColourIndex);
        builder.setIconName(mIcon.iconName);
        builder.setDeleted(mDeletedCheckBox.isChecked());
        builder.setActive(mActiveCheckBox.isChecked());

        return builder.build();
    }

    private void resetUI() {
        mDeletedEntry.setVisibility(View.GONE);
        mDeletedDivider.setVisibility(View.GONE);
        mDeletedCheckBox.setChecked(false);
        mActiveCheckBox.setChecked(true);

        if (mColourIndex == -1) {
            mColourIndex = 0;
        }

        displayIcon();
        displayColour();
        updatePreview();
    }

    private void updateUIFromItem(Context context) {
        mNameWidget.setTextKeepState(context.getName());

        mColourIndex = context.getColourIndex();
        displayColour();

        final String iconName = context.getIconName();
        mIcon = ContextIcon.createIcon(iconName, getResources());
        displayIcon();

        mActiveCheckBox.setChecked(context.isActive());

        mDeletedEntry.setVisibility(context.isDeleted() ? View.VISIBLE : View.GONE);
        mDeletedDivider.setVisibility(context.isDeleted() ? View.VISIBLE : View.GONE);
        mDeletedCheckBox.setChecked(context.isDeleted());

        updatePreview();

        if (mOriginalItem == null) {
            mOriginalItem = context;
        }
    }

    private void displayColour() {
        int bgColour = TextColours.getInstance(getActivity()).getBackgroundColour(mColourIndex);
        GradientDrawable drawable = DrawableUtils.createGradient(bgColour, GradientDrawable.Orientation.TL_BR);
        drawable.setCornerRadius(8.0f);
        mColourWidget.setBackgroundDrawable(drawable);
    }

    private void displayIcon() {
        if (mIcon == ContextIcon.NONE) {
            mIconNoneWidget.setVisibility(View.VISIBLE);
            mIconWidget.setVisibility(View.GONE);
            mClearIconButton.setEnabled(false);
        } else {
            mIconNoneWidget.setVisibility(View.GONE);
            mIconWidget.setImageResource(mIcon.largeIconId);
            mIconWidget.setVisibility(View.VISIBLE);
            mClearIconButton.setEnabled(true);
        }
    }

    private void updatePreview() {
		String name = mNameWidget.getText().toString();
		if (TextUtils.isEmpty(name) || mColourIndex == -1) {
			mContextPreview.setVisibility(View.INVISIBLE);
		} else {
			mContextPreview.updateView(createItemFromUI(false));
            mContextPreview.setVisibility(View.VISIBLE);
		}
    }
    
    private ActionBarFragmentActivity getActionBarFragmentActivity() {
        return (ActionBarFragmentActivity) getActivity();
    }

}
