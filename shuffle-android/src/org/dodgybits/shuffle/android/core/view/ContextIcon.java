package org.dodgybits.shuffle.android.core.view;

import android.content.res.Resources;
import android.text.TextUtils;
import org.dodgybits.android.shuffle.R;

public class ContextIcon {
    private static final String cPackage = "org.dodgybits.android.shuffle"; 
    private static final String cType = "drawable";
    
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
        if (TextUtils.isEmpty(iconName)) return nullForEmpty ? null : NONE;
        int largeId = res.getIdentifier(iconName, cType, cPackage);
        int smallId = res.getIdentifier(iconName + "_small", cType, cPackage);
        return new ContextIcon(iconName, largeId, smallId);
    }

}
