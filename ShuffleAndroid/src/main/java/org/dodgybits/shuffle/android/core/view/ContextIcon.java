package org.dodgybits.shuffle.android.core.view;

import android.content.res.Resources;
import android.text.TextUtils;
import org.dodgybits.android.shuffle.R;

public class ContextIcon {
    private static final String TAG = "ContextIcon";

    private static final String PACKAGE = "org.dodgybits.android.shuffle";
    private static final String TYPE = "drawable";
    
    public static final ContextIcon NONE = new ContextIcon(null, R.drawable.blank, R.drawable.blank_small);
    
    public final String iconName;
    public final int largeIconId;
    public final int smallIconId;
    
    private ContextIcon(String iconName, int largeIconId, int smallIconId) {
        this.iconName = iconName;
        this.largeIconId = largeIconId;
        this.smallIconId = smallIconId;
    }

    
    public static ContextIcon createIcon(String iconName, Resources res) {
        return createIcon(iconName, res, false);
    }

    public static ContextIcon createIcon(String iconName, Resources res, boolean nullForEmpty) {
        if (!TextUtils.isEmpty(iconName)) {
            int largeId = res.getIdentifier(iconName, TYPE, PACKAGE);
            int smallId = res.getIdentifier(iconName + "_small", TYPE, PACKAGE);
            if (largeId != 0 && smallId != 0) {
              return new ContextIcon(iconName, largeId, smallId);
            }
        }
        return nullForEmpty ? null : NONE;
    }

}
