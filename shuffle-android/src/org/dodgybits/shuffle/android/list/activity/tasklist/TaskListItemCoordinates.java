package org.dodgybits.shuffle.android.list.activity.tasklist;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.util.UiUtilities;

/**
 * Represents the coordinates of elements inside a TaskListItem
 * (eg, checkbox, project, description, contexts, etc.) It will inflate a view,
 * and record the coordinates of each element after layout. This will allows us
 * to easily improve performance by creating custom view while still defining
 * layout in XML files.
 */
public class TaskListItemCoordinates {

    private static int CONTENT_LENGTH;

    // Checkmark.
    int checkmarkX;
    int checkmarkY;
    int checkmarkWidthIncludingMargins;

    // Active and deleted state.
    int stateX;
    int stateY;

    // Project
    int projectX;
    int projectY;
    int projectWidth;
    int projectLineCount;
    int projectFontSize;
    int projectAscent;

    // Contents.
    int contentsX;
    int contentsY;
    int contentsWidth;
    int contentsLineCount;
    int contentsFontSize;
    int contentsAscent;

    // Contexts
    int contextsX;
    int contextsY;
    int contextsWidth;
    int contextsHeight;

    // Date.
    int dateXEnd;
    int dateY;
    int dateFontSize;
    int dateAscent;

    // Cache to save Coordinates based on view width.
    private static SparseArray<TaskListItemCoordinates> mCache =
            new SparseArray<TaskListItemCoordinates>();

    private static TextPaint sPaint = new TextPaint();

    static {
        sPaint.setTypeface(Typeface.DEFAULT);
        sPaint.setAntiAlias(true);
    }

    // Not directly instantiable.
    private TaskListItemCoordinates() {}

    /**
     * Returns a value array multiplied by the specified density.
     */
    public static int[] getDensityDependentArray(int[] values, float density) {
        int result[] = new int[values.length];
        for (int i = 0; i < values.length; ++i) {
            result[i] = (int) (values[i] * density);
        }
        return result;
    }

    /**
     * Returns the height of the view in this mode.
     */
    public static int getHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.task_list_item_height);
    }


    /**
     * Returns the width of a view.
     *
     * @param includeMargins whether or not to include margins when calculating
     *            width.
     */
    public static int getWidth(View view, boolean includeMargins) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        return view.getWidth() + (includeMargins ? params.leftMargin + params.rightMargin : 0);
    }

    /**
     * Returns the height of a view.
     *
     * @param includeMargins whether or not to include margins when calculating
     *            height.
     */
    public static int getHeight(View view, boolean includeMargins) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        return view.getHeight() + (includeMargins ? params.topMargin + params.bottomMargin : 0);
    }

    /**
     * Returns the number of lines of this text view.
     */
    private static int getLineCount(TextView textView) {
        return textView.getHeight() / textView.getLineHeight();
    }

    /**
     * Returns the length (maximum of characters) of contents in this mode.
     */
    public static int getContentsLength(Context context) {
        return context.getResources().getInteger(R.integer.content_length);
    }

    /**
     * Reset the caches associated with the coordinate layouts.
     */
    static void resetCaches() {
        mCache.clear();
    }

    /**
     * Returns coordinates for elements inside a conversation header view given
     * the view width.
     */
    public static TaskListItemCoordinates forWidth(Context context, int width) {
        TaskListItemCoordinates coordinates = mCache.get(width);
        if (coordinates == null) {
            coordinates = new TaskListItemCoordinates();
            mCache.put(width, coordinates);
            // TODO: make the field computation done inside of the constructor and mark fields final

            // Layout the appropriate view.
            int height = getHeight(context);
            View view = LayoutInflater.from(context).inflate(R.layout.task_list_item, null);
            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            view.measure(widthSpec, heightSpec);
            view.layout(0, 0, width, height);

            // Records coordinates.
            View checkmark = view.findViewById(R.id.checkmark);
            coordinates.checkmarkX = UiUtilities.getX(checkmark);
            coordinates.checkmarkY = UiUtilities.getY(checkmark);
            coordinates.checkmarkWidthIncludingMargins = getWidth(checkmark, true);

            View state = view.findViewById(R.id.active_state);
            coordinates.stateX = UiUtilities.getX(state);
            coordinates.stateY = UiUtilities.getY(state);

            TextView project = (TextView) view.findViewById(R.id.project);
            coordinates.projectX = UiUtilities.getX(project);
            coordinates.projectY = UiUtilities.getY(project);
            coordinates.projectWidth = getWidth(project, false);
            coordinates.projectLineCount = getLineCount(project);
            coordinates.projectFontSize = (int) project.getTextSize();
            coordinates.projectAscent = Math.round(project.getPaint().ascent());

            TextView contents = (TextView) view.findViewById(R.id.contents);
            coordinates.contentsX = UiUtilities.getX(contents);
            coordinates.contentsY = UiUtilities.getY(contents);
            coordinates.contentsWidth = getWidth(contents, false);
            coordinates.contentsLineCount = getLineCount(contents);
            coordinates.contentsFontSize = (int) contents.getTextSize();
            coordinates.contentsAscent = Math.round(contents.getPaint().ascent());

            View contexts = view.findViewById(R.id.contexts);
            coordinates.contextsX = UiUtilities.getX(contexts);
            coordinates.contextsY = UiUtilities.getY(contexts);
            coordinates.contextsWidth = getWidth(contexts, false);
            coordinates.contextsHeight = getHeight(contexts, false);

            TextView date = (TextView) view.findViewById(R.id.date);
            coordinates.dateXEnd = UiUtilities.getX(date) + date.getWidth();
            coordinates.dateY = UiUtilities.getY(date);
            coordinates.dateFontSize = (int) date.getTextSize();
            coordinates.dateAscent = Math.round(date.getPaint().ascent());
        }
        return coordinates;
    }
}
