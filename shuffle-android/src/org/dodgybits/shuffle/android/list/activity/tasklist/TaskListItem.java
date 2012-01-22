package org.dodgybits.shuffle.android.list.activity.tasklist;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.*;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import com.google.common.base.Objects;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Task;

/**
 * This custom View is the list item for the MessageList activity, and serves two purposes:
 * 1.  It's a container to store message metadata (e.g. the ids of the message, mailbox, & account)
 * 2.  It handles internal clicks such as the checkbox or the favorite star
 */
public class TaskListItem extends View {
    // Note: messagesAdapter directly fiddles with these fields.
    /* package */ long mTaskId;

    
    private Task mTask;

    private TaskListAdaptor mAdapter;
    private TaskListItemCoordinates mCoordinates;
    private Context mContext;

    private boolean mDownEvent;

    public TaskListItem(Context context) {
        super(context);
        init(context);
    }

    public TaskListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TaskListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private static boolean sInit = false;
    private static final TextPaint sDefaultPaint = new TextPaint();
    private static final TextPaint sBoldPaint = new TextPaint();
    private static final TextPaint sDatePaint = new TextPaint();
    private static int sBadgeMargin;
    private static Bitmap sSelectedIconOn;
    private static Bitmap sSelectedIconOff;
    private static Bitmap sStateInactive;
    private static Bitmap sStateDeleted;
    private static String sContentsSnippetDivider;

    // Static colors.
    private static int ACTIVATED_TEXT_COLOR;
    private static int DESCRIPTION_TEXT_COLOR_COMPLETE;
    private static int DESCRIPTION_TEXT_COLOR_INCOMPLETE;
    private static int SNIPPET_TEXT_COLOR_COMPLETE;
    private static int SNIPPET_TEXT_COLOR_INCOMPLETE;
    private static int PROJECT_TEXT_COLOR_COMPLETE;
    private static int PROJECT_TEXT_COLOR_INCOMPLETE;
    private static int DATE_TEXT_COLOR_COMPLETE;
    private static int DATE_TEXT_COLOR_INCOMPLETE;

    public String mProject;
    public SpannableStringBuilder mText;
    public String mSnippet;
    private String mDescription;
    private StaticLayout mContentsLayout;
    public boolean mCompleted;
    public boolean mIsActive = true;
    public boolean mIsDeleted = false;

    private int mViewWidth = 0;
    private int mViewHeight = 0;

    private static int sItemHeight;

    // Note: these cannot be shared Drawables because they are selectors which have state.
    private Drawable mCompleteSelector;
    private Drawable mIncompleteSelector;

    private CharSequence mFormattedProject;
    // We must initialize this to something, in case the timestamp of the message is zero (which
    // should be very rare); this is otherwise set in setTimestamp
    private CharSequence mFormattedDate = "";

    private void init(Context context) {
        mContext = context;
        if (!sInit) {
            Resources r = context.getResources();
            sContentsSnippetDivider = r.getString(R.string.task_list_contents_snippet_divider);
            sItemHeight =
                    r.getDimensionPixelSize(R.dimen.task_list_item_height);

            sDefaultPaint.setTypeface(Typeface.DEFAULT);
            sDefaultPaint.setAntiAlias(true);
            sDatePaint.setTypeface(Typeface.DEFAULT);
            sDatePaint.setAntiAlias(true);
            sBoldPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sBoldPaint.setAntiAlias(true);

            sBadgeMargin = r.getDimensionPixelSize(R.dimen.message_list_badge_margin);
            sSelectedIconOff =
                    BitmapFactory.decodeResource(r, R.drawable.btn_check_off_normal_holo_light);
            sSelectedIconOn =
                    BitmapFactory.decodeResource(r, R.drawable.btn_check_on_normal_holo_light);

            sStateInactive =
                    BitmapFactory.decodeResource(r, R.drawable.ic_badge_reply_holo_light);
            sStateDeleted =
                    BitmapFactory.decodeResource(r, R.drawable.ic_badge_forward_holo_light);

            ACTIVATED_TEXT_COLOR = r.getColor(android.R.color.black);
            DESCRIPTION_TEXT_COLOR_COMPLETE = r.getColor(R.color.description_text_color_complete);
            DESCRIPTION_TEXT_COLOR_INCOMPLETE = r.getColor(R.color.description_text_color_incomplete);
            SNIPPET_TEXT_COLOR_COMPLETE = r.getColor(R.color.snippet_text_color_complete);
            SNIPPET_TEXT_COLOR_INCOMPLETE = r.getColor(R.color.snippet_text_color_incomplete);
            PROJECT_TEXT_COLOR_COMPLETE = r.getColor(R.color.project_text_color_complete);
            PROJECT_TEXT_COLOR_INCOMPLETE = r.getColor(R.color.project_text_color_incomplete);
            DATE_TEXT_COLOR_COMPLETE = r.getColor(R.color.date_text_color_complete);
            DATE_TEXT_COLOR_INCOMPLETE = r.getColor(R.color.date_text_color_incomplete);

            sInit = true;
        }
    }

    /**
     * Invalidate all drawing caches associated with drawing message list items.
     * This is an expensive operation, and should be done rarely, such as when system font size
     * changes occurs.
     */
    public static void resetDrawingCaches() {
        TaskListItemCoordinates.resetCaches();
        sInit = false;
    }


    public void setTask(Task task) {
        mTask = task;

        mTaskId = task.getLocalId().getId();
        mCompleted = task.isComplete();
        mProject = task.getProjectId().toString();
        mIsActive = task.isActive();
        mIsDeleted = task.isDeleted();
        setText(task.getDescription(), task.getDetails(), false);
        setTimestamp(task.getDueDate());
    }

    /**
     * Sets message contents and snippet safely, ensuring the cache is invalidated.
     */
    private void setText(String description, String snippet, boolean forceUpdate) {
        boolean changed = false;
        if (!Objects.equal(mDescription, description)) {
            mDescription = description;
            changed = true;
        }

        if (!Objects.equal(mSnippet, snippet)) {
            mSnippet = snippet;
            changed = true;
        }

        if (forceUpdate || changed || (mDescription == null && mSnippet == null) /* first time */) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            boolean hasContents = false;
            if (!TextUtils.isEmpty(mDescription)) {
                SpannableString ss = new SpannableString(mDescription);
                ss.setSpan(new StyleSpan(mCompleted ? Typeface.NORMAL : Typeface.BOLD), 0, ss.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.append(ss);
                hasContents = true;
            }
            if (!TextUtils.isEmpty(mSnippet)) {
                if (hasContents) {
                    ssb.append(sContentsSnippetDivider);
                }
                ssb.append(mSnippet);
            }
            mText = ssb;
            requestLayout();
        }
    }

    long mTimeFormatted = 0;
    private void setTimestamp(long timestamp) {
        if (mTimeFormatted != timestamp) {
            mFormattedDate = DateUtils.getRelativeTimeSpanString(mContext, timestamp).toString();
            mTimeFormatted = timestamp;
        }
    }

    private Drawable mCurentBackground = null; // Only used by updateBackground()

    private void updateBackground() {
        final Drawable newBackground;
        if (mCompleted) {
            if (mCompleteSelector == null) {
                mCompleteSelector = getContext().getResources()
                        .getDrawable(R.drawable.task_complete_selector);
            }
            newBackground = mCompleteSelector;
        } else {
            if (mIncompleteSelector == null) {
                mIncompleteSelector = getContext().getResources()
                        .getDrawable(R.drawable.task_incomplete_selector);
            }
            newBackground = mIncompleteSelector;
        }
        if (newBackground != mCurentBackground) {
            // setBackgroundDrawable is a heavy operation.  Only call it when really needed.
            setBackgroundDrawable(newBackground);
            mCurentBackground = newBackground;
        }
    }

    private void calculateContentsText() {
        if (mText == null || mText.length() == 0) {
            return;
        }
        boolean hasContents = false;
        int snippetStart = 0;
        if (!TextUtils.isEmpty(mDescription)) {
            int contentsColor = getFontColor(mCompleted ? DESCRIPTION_TEXT_COLOR_COMPLETE
                    : DESCRIPTION_TEXT_COLOR_INCOMPLETE);
            mText.setSpan(new ForegroundColorSpan(contentsColor), 0, mDescription.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            snippetStart = mDescription.length() + 1;
        }
        if (!TextUtils.isEmpty(mSnippet)) {
            int snippetColor = getFontColor(mCompleted ? SNIPPET_TEXT_COLOR_COMPLETE
                    : SNIPPET_TEXT_COLOR_INCOMPLETE);
            mText.setSpan(new ForegroundColorSpan(snippetColor), snippetStart, mText.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void calculateDrawingData() {
        sDefaultPaint.setTextSize(mCoordinates.contentsFontSize);
        calculateContentsText();
        mContentsLayout = new StaticLayout(mText, sDefaultPaint,
                mCoordinates.contentsWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false /* includePad */);
        if (mCoordinates.contentsLineCount < mContentsLayout.getLineCount()) {
            // TODO: ellipsize.
            int end = mContentsLayout.getLineEnd(mCoordinates.contentsLineCount - 1);
            mContentsLayout = new StaticLayout(mText.subSequence(0, end),
                    sDefaultPaint, mCoordinates.contentsWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        }

        // Now, format the project for its width
        TextPaint projectPaint = mCompleted ? sDefaultPaint : sBoldPaint;
        // And get the ellipsized string for the calculated width
        if (TextUtils.isEmpty(mProject)) {
            mFormattedProject = "";
        } else {
            int projectWidth = mCoordinates.projectWidth;
            projectPaint.setTextSize(mCoordinates.projectFontSize);
            projectPaint.setColor(getFontColor(mCompleted ? PROJECT_TEXT_COLOR_COMPLETE
                    : PROJECT_TEXT_COLOR_INCOMPLETE));
            mFormattedProject = TextUtils.ellipsize(mProject, projectPaint, projectWidth,
                    TextUtils.TruncateAt.END);
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (widthMeasureSpec != 0 || mViewWidth == 0) {
            mViewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
            mViewHeight = measureHeight(heightMeasureSpec);
        }
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    /**
     * Determine the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = sItemHeight;
            if (specMode == View.MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by
                // measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    public void draw(Canvas canvas) {
        // Update the background, before View.draw() draws it.
        setSelected(mAdapter.isSelected(this));
        updateBackground();
        super.draw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mCoordinates = TaskListItemCoordinates.forWidth(mContext, mViewWidth);
        calculateDrawingData();
    }

    private int getFontColor(int defaultColor) {
        return  (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && isActivated()) ? ACTIVATED_TEXT_COLOR : defaultColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the checkbox
        canvas.drawBitmap(mAdapter.isSelected(this) ? sSelectedIconOn : sSelectedIconOff,
                mCoordinates.checkmarkX, mCoordinates.checkmarkY, null);

        // Draw the project name
        Paint projectPaint = mCompleted ? sDefaultPaint : sBoldPaint;
        projectPaint.setColor(getFontColor(mCompleted ? PROJECT_TEXT_COLOR_COMPLETE
                : PROJECT_TEXT_COLOR_INCOMPLETE));
        projectPaint.setTextSize(mCoordinates.projectFontSize);
        canvas.drawText(mFormattedProject, 0, mFormattedProject.length(),
                mCoordinates.projectX, mCoordinates.projectY - mCoordinates.projectAscent,
                projectPaint);

        // Draw the reply state. Draw nothing if neither replied nor forwarded.
        if (mIsDeleted) {
            canvas.drawBitmap(sStateDeleted,
                    mCoordinates.stateX, mCoordinates.stateY, null);
        } else if (mIsActive) {
            canvas.drawBitmap(sStateInactive,
                    mCoordinates.stateX, mCoordinates.stateY, null);
        }

        // Contents and snippet.
        sDefaultPaint.setTextSize(mCoordinates.contentsFontSize);
        canvas.save();
        canvas.translate(
                mCoordinates.contentsX,
                mCoordinates.contentsY);
        mContentsLayout.draw(canvas);
        canvas.restore();

        // Draw the date
        sDatePaint.setTextSize(mCoordinates.dateFontSize);
        sDatePaint.setColor(mCompleted ? DATE_TEXT_COLOR_COMPLETE : DATE_TEXT_COLOR_INCOMPLETE);
        int dateX = mCoordinates.dateXEnd
                - (int) sDatePaint.measureText(mFormattedDate, 0, mFormattedDate.length());

        canvas.drawText(mFormattedDate, 0, mFormattedDate.length(),
                dateX, mCoordinates.dateY - mCoordinates.dateAscent, sDatePaint);

    }

    /**
     * Called by the adapter at bindView() time
     *
     * @param adapter the adapter that creates this view
     */
    public void bindViewInit(TaskListAdaptor adapter) {
        mAdapter = adapter;
        requestLayout();
    }

    private static final int TOUCH_SLOP = 24;
    private static int sScaledTouchSlop = -1;

    private void initializeSlop(Context context) {
        if (sScaledTouchSlop == -1) {
            final Resources res = context.getResources();
            final Configuration config = res.getConfiguration();
            final float density = res.getDisplayMetrics().density;
            final float sizeAndDensity;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
                    config.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_XLARGE)) {
                sizeAndDensity = density * 1.5f;
            } else {
                sizeAndDensity = density;
            }
            sScaledTouchSlop = (int) (sizeAndDensity * TOUCH_SLOP + 0.5f);
        }
    }

    /**
     * Overriding this method allows us to "catch" clicks in the checkbox or star
     * and process them accordingly.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initializeSlop(getContext());

        boolean handled = false;
        int touchX = (int) event.getX();
        int checkRight = mCoordinates.checkmarkX
                + mCoordinates.checkmarkWidthIncludingMargins + sScaledTouchSlop;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (touchX < checkRight) {
                    mDownEvent = true;
                    handled = true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mDownEvent = false;
                break;

            case MotionEvent.ACTION_UP:
                if (mDownEvent) {
                    if (touchX < checkRight) {
                        mAdapter.toggleSelected(this);
                        handled = true;
                    }
                }
                break;
        }

        if (handled) {
            invalidate();
        } else {
            handled = super.onTouchEvent(event);
        }

        return handled;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.setClassName(getClass().getName());
        event.setPackageName(getContext().getPackageName());
        event.setEnabled(true);
        event.setContentDescription(getContentDescription());
        return true;
    }

}