package org.dodgybits.shuffle.android.list.view.context;

import android.graphics.drawable.GradientDrawable;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.util.TextColours;
import org.dodgybits.shuffle.android.core.view.ContextIcon;
import org.dodgybits.shuffle.android.core.view.DrawableUtils;
import org.dodgybits.shuffle.android.list.view.StatusView;

public class ContextListItem extends LinearLayout {
    protected TextColours mTextColours;
    private ImageView mIcon;
    private TextView mName;
    private TextView mCount;
    private StatusView mStatus;
    private SparseIntArray mTaskCountArray;

    @Inject
    public ContextListItem(android.content.Context context) {
        super(context);
        init(context);
    }

    public void init(android.content.Context androidContext) {
        LayoutInflater vi = (LayoutInflater)androidContext.
                getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(R.layout.context_view, this, true);

        mName = (TextView) findViewById(R.id.name);
        mCount = (TextView) findViewById(R.id.count);
        mStatus = (StatusView)findViewById(R.id.status_view);
        mIcon = (ImageView) findViewById(R.id.icon);
        mTextColours = TextColours.getInstance(androidContext);
    }

    public void setTaskCountArray(SparseIntArray taskCountArray) {
        mTaskCountArray = taskCountArray;
    }

    public void updateView(Context context) {
        updateIcon(context);
        updateName(context);
        updateCount(context);
        updateStatus(context);
    }

    private void updateIcon(Context context) {
        ContextIcon icon = ContextIcon.createIcon(context.getIconName(), getResources());
        int iconResource = icon.largeIconId;
        if (iconResource > 0) {
            mIcon.setImageResource(iconResource);
            mIcon.setVisibility(View.VISIBLE);

            int bgColour = mTextColours.getBackgroundColour(context.getColourIndex());
            GradientDrawable drawable = DrawableUtils.createGradient(bgColour, GradientDrawable.Orientation.TOP_BOTTOM);
            drawable.setCornerRadius(16.0f);
            mIcon.setBackgroundDrawable(drawable);
        } else {
            mIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void updateName(Context context) {
        mName.setText(context.getName());
    }
    
    private void updateCount(Context context) {
        if (mTaskCountArray != null) {
            Integer count = mTaskCountArray.get((int)context.getLocalId().getId());
            if (count == null) count = 0;
            mCount.setText(String.valueOf(count));
        } else {
            mCount.setText("");
        }
    }

    private void updateStatus(Context context) {
        if (mStatus != null) {
            mStatus.updateStatus(context.isActive(), context.isDeleted(), false);
        }
    }

}
