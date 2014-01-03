package org.dodgybits.shuffle.android.core.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

public class FontUtils {
    private static Typeface regular;

    private static Typeface bold;

    private static Typeface allcaps;

    private static void processsViewGroup(ViewGroup v, final int len) {

        for (int i = 0; i < len; i++) {
            final View c = v.getChildAt(i);
            if (c instanceof TextView) {
                setCustomFont((TextView) c);
            } else if (c instanceof ViewGroup) {
                setCustomFont((ViewGroup) c);
            }
        }
    }

    private static void setCustomFont(TextView view) {
        Object tag = view.getTag();
        if (tag instanceof String) {
            if (((String) tag).contains("bold")) {
                view.setTypeface(bold);
                return;
            }
            if (((String) tag).contains("allcaps")) {
                view.setTypeface(allcaps);
                return;
            }
        }
        view.setTypeface(regular);
    }

    public static void setCustomFont(TextPaint textPaint, AssetManager assetsManager, String style) {
        initTypefaces(assetsManager);
        if ("bold".equals(style)) {
            textPaint.setTypeface(bold);
        } else if ("allcaps".equals(style)) {
            textPaint.setTypeface(allcaps);
        } else {
            textPaint.setTypeface(regular);
        }
    }

    public static void setCustomFont(View topView, AssetManager assetsManager) {
        initTypefaces(assetsManager);
        if (topView instanceof ViewGroup) {
            setCustomFont((ViewGroup) topView);
        } else if (topView instanceof TextView) {
            setCustomFont((TextView) topView);
        }
    }

    private static void initTypefaces(AssetManager assetsManager) {
        if (regular == null || bold == null || allcaps == null) {
            regular = Typeface.createFromAsset(assetsManager, "Concourse T3 Regular.otf");
            bold = Typeface.createFromAsset(assetsManager, "Concourse T3 Bold.otf");
            allcaps = Typeface.createFromAsset(assetsManager, "Concourse C6 Regular.otf");
        }
    }

    private static void setCustomFont(ViewGroup v) {
        final int len = v.getChildCount();
        processsViewGroup(v, len);
    }
}