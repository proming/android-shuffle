package org.dodgybits.shuffle.android.core.view;

import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.dodgybits.android.shuffle.R;

public class IconNameCountListItem extends LinearLayout {
    private ImageView mIcon;
    private TextView mName;
    private TextView mCount;

    public IconNameCountListItem(android.content.Context context) {
        super(context);
        init(context);
    }

    public void init(android.content.Context androidContext) {
        LayoutInflater vi = (LayoutInflater)androidContext.
                getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(R.layout.icon_count_list_view, this, true);

        mName = (TextView) findViewById(R.id.name);
        mCount = (TextView) findViewById(R.id.count);
        mIcon = (ImageView) findViewById(R.id.icon);
    }

    public void updateView(String name, String count, int iconResId) {
        mName.setText(name);
        mCount.setText(count);
        mIcon.setImageResource(iconResId);
    }

}
