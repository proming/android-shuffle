package org.dodgybits.shuffle.android.list.view.task;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.text.*;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.util.OSUtils;
import org.dodgybits.shuffle.android.core.view.TextColours;
import org.dodgybits.shuffle.android.core.view.ContextIcon;

import java.util.List;
import java.util.Map;

/**
 * This custom View is the list item for the MessageList activity, and serves two purposes:
 * 1.  It's a container to store message metadata (e.g. the ids of the message, mailbox, & account)
 * 2.  It handles internal clicks such as the checkbox or the favorite star
 */
public class TaskListItem extends View {
    private static final String TAG = "TaskListItem";

    // Note: messagesAdapter directly fiddles with these fields.
    /* package */ long mTaskId;

    
    private Task mTask;

    private TaskListAdaptor mAdapter;
    private TaskListItemCoordinates mCoordinates;
    private Context mAndroidContext;

    private final EntityCache<org.dodgybits.shuffle.android.core.model.Context> mContextCache;
    private final EntityCache<Project> mProjectCache;
    
    private boolean mDownEvent;

    @Inject
    public TaskListItem(
            android.content.Context androidContext,
            EntityCache<org.dodgybits.shuffle.android.core.model.Context> contextCache,
            EntityCache<Project> projectCache) {
        super(androidContext);
        mContextCache = contextCache;
        mProjectCache = projectCache;
        init(androidContext);
    }

    private static boolean sInit = false;
    private static TextColours sTextColours;
    private static final TextPaint sDefaultPaint = new TextPaint();
    private static final TextPaint sBoldPaint = new TextPaint();
    private static final TextPaint sDatePaint = new TextPaint();
    private static final TextPaint sContextPaint = new TextPaint();
    private static final Paint sContextBackgroundPaint = new Paint();

    private static int sContextHorizontalPadding;
    private static int sContextVerticalPadding;
    private static int sContextCornerRadius;
    
    private static Bitmap sSelectedIconOn;
    private static Bitmap sSelectedIconOff;
    private static Bitmap sStateInactive;
    private static Bitmap sStateDeleted;
    private static Bitmap sStateCompleted;

    private static String sContentsSnippetDivider;
    
    private static Map<String, Bitmap> mContextIconMap;
    
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
    private Project mProject;
    private SpannableStringBuilder mText;
    private String mSnippet;
    private String mDescription;
    private StaticLayout mContentsLayout;
    private boolean mIsCompleted;
    private boolean mIsActive = true;
    private boolean mIsDeleted = false;
    private List<org.dodgybits.shuffle.android.core.model.Context> mContexts;
    private String mContextName;
    private int mContextTextColor;
    private int mContextBackgroundColor;
    private CharSequence mFormattedContext;

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
        mAndroidContext = context;
        
        if (!sInit) {
            sTextColours = TextColours.getInstance(context);
            Resources r = context.getResources();
            sContentsSnippetDivider = r.getString(R.string.task_list_contents_snippet_divider);
            sItemHeight =
                    r.getDimensionPixelSize(R.dimen.list_item_height);

            sDefaultPaint.setTypeface(Typeface.DEFAULT);
            sDefaultPaint.setAntiAlias(true);
            sDatePaint.setTypeface(Typeface.DEFAULT);
            sDatePaint.setAntiAlias(true);
            sBoldPaint.setTypeface(Typeface.DEFAULT_BOLD);
            sBoldPaint.setAntiAlias(true);
            sBoldPaint.setShadowLayer(0f, 1.0f, 1.0f, R.color.white);
            sContextPaint.setTypeface(Typeface.DEFAULT);
            sContextPaint.setAntiAlias(true);

            sContextHorizontalPadding = r.getDimensionPixelSize(R.dimen.context_small_horizontal_padding);
            sContextVerticalPadding = r.getDimensionPixelSize(R.dimen.context_small_vertical_padding);
            sContextCornerRadius = r.getDimensionPixelSize(R.dimen.context_small_corner_radius);
            
            sSelectedIconOff =
                    BitmapFactory.decodeResource(r, R.drawable.btn_check_off_normal_holo_light);
            sSelectedIconOn =
                    BitmapFactory.decodeResource(r, R.drawable.btn_check_on_normal_holo_light);

            sStateInactive =
                    BitmapFactory.decodeResource(r, R.drawable.ic_badge_inactive);
            sStateDeleted =
                    BitmapFactory.decodeResource(r, R.drawable.ic_badge_delete);
            sStateCompleted =
                    BitmapFactory.decodeResource(r, R.drawable.ic_badge_complete);

            mContextIconMap = Maps.newHashMap();        
    
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
    
    private Bitmap getContextIcon(String iconName) {
        Bitmap icon = mContextIconMap.get(iconName);
        if (icon == null) {
            ContextIcon contextIcon = ContextIcon.createIcon(iconName, mAndroidContext.getResources());
            icon = BitmapFactory.decodeResource(mAndroidContext.getResources(), contextIcon.smallIconId);
            mContextIconMap.put(iconName, icon);
        }
        return icon;
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
        mIsCompleted = task.isComplete();
        setProject(task.getProjectId());
        mIsActive = task.isActive() && (mProject == null || mProject.isActive()); // TODO && (mContext == null || mContext.isActive());
        mIsDeleted = task.isDeleted() || (mProject != null && mProject.isDeleted()); // TODO || (mContext != null && mContext.isDeleted());
        setTimestamp(task.getDueDate());

        boolean changed = setContexts(task.getContextIds());
        changed |= setText(task.getDescription(), task.getDetails());
        
        if (changed) {
            requestLayout();
        }
    }

    private void setProject(Id projectId) {
        mProject = mProjectCache.findById(projectId);
    }
    
    private boolean setContexts(List<Id> contextIds) {
        boolean changed = false;

        // TODO use all contexts
        mContexts = mContextCache.findById(contextIds);
        org.dodgybits.shuffle.android.core.model.Context context = null;
        if (!mContexts.isEmpty()) {
            context = mContexts.get(0);
            mContextTextColor = sTextColours.getTextColour(context.getColourIndex());
            mContextBackgroundColor = sTextColours.getBackgroundColour(context.getColourIndex());
        }

        String contextName = context == null ? "" : context.getName();
        if (!Objects.equal(mContextName, contextName)) {
            mContextName = contextName;
            changed = true;
        }
        
        return changed;
    }
    
    /**
     * Sets message contents and snippet safely, ensuring the cache is invalidated.
     */
    private boolean setText(String description, String snippet) {
        boolean changed = false;
        if (!Objects.equal(mDescription, description)) {
            mDescription = description;
            changed = true;
        }

        if (!Objects.equal(mSnippet, snippet)) {
            mSnippet = snippet;
            changed = true;
        }

        if (changed || (mDescription == null && mSnippet == null) /* first time */) {
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            boolean hasContents = false;
            if (!TextUtils.isEmpty(mDescription)) {
                SpannableString ss = new SpannableString(mDescription);
                ss.setSpan(new StyleSpan(mIsCompleted ? Typeface.NORMAL : Typeface.BOLD), 0, ss.length(),
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
            changed = true;
        }
        
        return changed;
    }

    long mTimeFormatted = 0L;
    private void setTimestamp(long timestamp) {
        if (mTimeFormatted != timestamp) {
            mFormattedDate = timestamp == 0L ? "" :
                    DateUtils.getRelativeTimeSpanString(mAndroidContext, timestamp).toString();
            mTimeFormatted = timestamp;
        }
    }

    private boolean isDone() {
        return mIsCompleted || mIsDeleted;
    }

    private Drawable mCurrentBackground = null; // Only used by updateBackground()

    private void updateBackground() {
        final Drawable newBackground;
        if (isDone()) {
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
        if (newBackground != mCurrentBackground) {
            // setBackgroundDrawable is a heavy operation.  Only call it when really needed.
            setBackgroundDrawable(newBackground);
            mCurrentBackground = newBackground;
        }
    }

    private void calculateContentsText() {
        if (mText == null || mText.length() == 0) {
            return;
        }
        int snippetStart = 0;
        if (!TextUtils.isEmpty(mDescription)) {
            int contentsColor = getFontColor(isDone() ? DESCRIPTION_TEXT_COLOR_COMPLETE
                    : DESCRIPTION_TEXT_COLOR_INCOMPLETE);
            mText.setSpan(new ForegroundColorSpan(contentsColor), 0, mDescription.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            snippetStart = mDescription.length() + 1;
        }
        if (!TextUtils.isEmpty(mSnippet)) {
            int snippetColor = getFontColor(isDone() ? SNIPPET_TEXT_COLOR_COMPLETE
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
            int end = mContentsLayout.getLineEnd(mCoordinates.contentsLineCount - 1);
            mContentsLayout = new StaticLayout(mText.subSequence(0, end),
                    sDefaultPaint, mCoordinates.contentsWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        }

        // Calculate the size the context wants to be
        TextPaint contextPaint = sContextPaint;
        contextPaint.setTextSize(mCoordinates.contextsFontSize);
        contextPaint.setColor(mContextTextColor);
        int contextTextWidth = (int)sContextPaint.measureText(mContextName, 0, mContextName.length());

        // And the project...
        TextPaint projectPaint = isDone() ? sDefaultPaint : sBoldPaint;
        String projectName = mProject == null ? "" : mProject.getName();
        projectPaint.setTextSize(mCoordinates.projectFontSize);
        projectPaint.setColor(getFontColor(isDone() ? PROJECT_TEXT_COLOR_COMPLETE
                : PROJECT_TEXT_COLOR_INCOMPLETE));
        int projectTextWidth = (int)projectPaint.measureText(projectName, 0, projectName.length());
        
        // if project needs less that it's given, give that to the context...
        int spareWidth = Math.max(0, mCoordinates.projectWidth - projectTextWidth);
        int contextWidth = mCoordinates.contextsWidth + spareWidth;
        mFormattedContext = TextUtils.ellipsize(mContextName, contextPaint, contextWidth,
                TextUtils.TruncateAt.END);

        // and visa versa
        spareWidth = Math.max(0, mCoordinates.contextsWidth - contextTextWidth);
        int projectWidth = mCoordinates.projectWidth + spareWidth;
        mFormattedProject = TextUtils.ellipsize(projectName, projectPaint, projectWidth,
                TextUtils.TruncateAt.END);
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

        mCoordinates = TaskListItemCoordinates.forWidth(mAndroidContext, mViewWidth);
        calculateDrawingData();
    }

    private int getFontColor(int defaultColor) {
        return  (OSUtils.atLeastHoneycomb() && isActivated()) ? ACTIVATED_TEXT_COLOR : defaultColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the checkbox
        canvas.drawBitmap(mAdapter.isSelected(this) ? sSelectedIconOn : sSelectedIconOff,
                mCoordinates.checkmarkX, mCoordinates.checkmarkY, null);

        // Draw the project name
        Paint projectPaint = isDone() ? sDefaultPaint : sBoldPaint;
        projectPaint.setColor(getFontColor(isDone() ? PROJECT_TEXT_COLOR_COMPLETE
                : PROJECT_TEXT_COLOR_INCOMPLETE));
        projectPaint.setTextSize(mCoordinates.projectFontSize);
        canvas.drawText(mFormattedProject, 0, mFormattedProject.length(),
                mCoordinates.projectX, mCoordinates.projectY - mCoordinates.projectAscent,
                projectPaint);

        // Draw the task state. Most important being deleted, then complete, then inactive
        if (mIsDeleted) {
            canvas.drawBitmap(sStateDeleted,
                    mCoordinates.stateX, mCoordinates.stateY, null);
        } else if (mIsCompleted) {
            canvas.drawBitmap(sStateCompleted,
                    mCoordinates.stateX, mCoordinates.stateY, null);
        } else if (!mIsActive) {
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
        sDatePaint.setColor(mIsCompleted ? DATE_TEXT_COLOR_COMPLETE : DATE_TEXT_COLOR_INCOMPLETE);
        int dateX = mCoordinates.dateXEnd
                - (int) sDatePaint.measureText(mFormattedDate, 0, mFormattedDate.length());

        canvas.drawText(mFormattedDate, 0, mFormattedDate.length(),
                dateX, mCoordinates.dateY - mCoordinates.dateAscent, sDatePaint);

        // Draw the context
        if (!mContexts.isEmpty()) {
            sContextPaint.setTextSize(mCoordinates.contextsFontSize);
            sContextPaint.setColor(mContextTextColor);
            int contextTextWidth = (int)sContextPaint.measureText(mFormattedContext, 0, mFormattedContext.length());
            int contextsX = dateX - (contextTextWidth + sContextHorizontalPadding * 2);

            // TODO use them all
            org.dodgybits.shuffle.android.core.model.Context context = mContexts.get(0);

            boolean hasIcon = !TextUtils.isEmpty(context.getIconName());
            int contextIconX = contextsX - (mCoordinates.contextIconWidth + sContextHorizontalPadding);
            
            RectF backgroundRectF;
            if (hasIcon)
            {
                backgroundRectF = new RectF(
                        contextIconX - sContextHorizontalPadding,
                        mCoordinates.contextsY - sContextVerticalPadding,
                        contextsX + contextTextWidth + sContextHorizontalPadding,
                        mCoordinates.contextsY + mCoordinates.contextsHeight + sContextVerticalPadding);
            } else {
                backgroundRectF = new RectF(
                        contextsX - sContextHorizontalPadding,
                        mCoordinates.contextsY - sContextVerticalPadding,
                        contextsX + contextTextWidth + sContextHorizontalPadding,
                        mCoordinates.contextsY + mCoordinates.contextsHeight + sContextVerticalPadding);
            }
            sContextBackgroundPaint.setShader(getShader(mContextBackgroundColor, backgroundRectF));
            canvas.drawRoundRect(backgroundRectF, sContextCornerRadius, sContextCornerRadius, sContextBackgroundPaint);

            if (hasIcon) {
                Bitmap contextIcon = getContextIcon(context.getIconName());
                canvas.drawBitmap(contextIcon, contextIconX, mCoordinates.contextsY, null);
            }

            canvas.drawText(mFormattedContext, 0, mFormattedContext.length(),
                    contextsX, mCoordinates.contextsY - mCoordinates.contextsAscent,
                    sContextPaint);
        }
    }
    
    private Shader getShader(int colour, RectF rect) {
        final float startOffset = 1.1f;
        final float endOffset = 0.9f;
        
        int[] colours = new int[2];
        float[] hsv1 = new float[3];
        float[] hsv2 = new float[3];
        Color.colorToHSV(colour, hsv1);
        Color.colorToHSV(colour, hsv2);
        hsv1[2] *= startOffset;
        hsv2[2] *= endOffset;
        colours[0] = Color.HSVToColor(hsv1);
        colours[1] = Color.HSVToColor(hsv2);

        return new LinearGradient(rect.left, rect.top, rect.left, rect.bottom,
                colours, null, Shader.TileMode.CLAMP);
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
            if (OSUtils.atLeastHoneycomb() &&
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