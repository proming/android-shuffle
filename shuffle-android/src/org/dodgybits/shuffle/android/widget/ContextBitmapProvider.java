package org.dodgybits.shuffle.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.widget.RelativeLayout;
import org.dodgybits.shuffle.android.core.util.TextColours;
import org.dodgybits.shuffle.android.core.view.DrawableUtils;

import java.util.HashMap;

public class ContextBitmapProvider {
    private static final Bitmap sEmptyBitmap = Bitmap.createBitmap(8, 40, Bitmap.Config.ARGB_8888);

    private HashMap<Integer, Bitmap> mGradientCache;
    private TextColours mColours;
    private Context mAndroidContext;

    public ContextBitmapProvider(Context androidContext) {
        mAndroidContext = androidContext;
        mColours = TextColours.getInstance(androidContext);
        mGradientCache = new HashMap<Integer, Bitmap>(mColours.getNumColours());
    }

    public Bitmap getBitmapForContext(org.dodgybits.shuffle.android.core.model.Context context) {
        Bitmap gradientBitmap = sEmptyBitmap;
        if (context != null) {
            int colourIndex = context.getColourIndex();
            gradientBitmap = mGradientCache.get(colourIndex);
            if (gradientBitmap == null) {
                int colour = mColours.getBackgroundColour(colourIndex);
                GradientDrawable drawable = DrawableUtils.createGradient(colour, GradientDrawable.Orientation.TOP_BOTTOM);
                drawable.setCornerRadius(6.0f);

                Bitmap bitmap = Bitmap.createBitmap(16, 80, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                RelativeLayout l = new RelativeLayout(mAndroidContext);
                l.setBackgroundDrawable(drawable);
                l.layout(0, 0, 16, 80);
                l.draw(canvas);
                gradientBitmap = Bitmap.createBitmap(bitmap, 6, 0, 10, 80);
                mGradientCache.put(colourIndex, gradientBitmap);
            }
        }
        return gradientBitmap;
    }
}
