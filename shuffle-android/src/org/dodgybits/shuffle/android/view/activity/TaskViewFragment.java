package org.dodgybits.shuffle.android.view.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.inject.Inject;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.encoding.TaskEncoder;
import org.dodgybits.shuffle.android.core.model.persistence.EntityCache;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.util.CalendarUtils;
import org.dodgybits.shuffle.android.core.view.ContextIcon;
import org.dodgybits.shuffle.android.list.view.LabelView;
import org.dodgybits.shuffle.android.list.view.StatusView;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import roboguice.fragment.RoboFragment;

public class TaskViewFragment extends RoboFragment implements View.OnClickListener {
    public static final String SELECTED_INDEX = "selectedIndex";
    public static final String TASK_LIST_CONTEXT = "taskListContext";


    public static final String INDEX = "TaskViewFragment.index";
    public static final String COUNT = "TaskViewFragment.count";

    private @Inject
    TaskEncoder mEncoder;
    
    private Button mEditButton;
    
    private Button mCompleteButton;

    private TextView mProjectView;
    private TextView mDescriptionView;
    private LabelView mContextView;

    private View mDetailsEntry;
    private TextView mDetailsView;

    private TextView mStartView;
    private TextView mDueView;

    private View mCalendarEntry;
    private Button mViewCalendarButton;

    private StatusView mStatusView;
    private TextView mCompletedView;
    private TextView mCreatedView;
    private TextView mModifiedView;

    @Inject private EntityCache<Project> mProjectCache;
    @Inject private EntityCache<Context> mContextCache;
    @Inject private TaskPersister mPersister;

    private Task mTask;
    private int mPosition;
    private int mTaskCount;

    public static TaskViewFragment newInstance(Bundle args) {
        TaskViewFragment fragment = new TaskViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mTask = mEncoder.restore(args);
            mTaskCount = args.getInt(COUNT, -1);
            mPosition = args.getInt(INDEX, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentx
        return inflater.inflate(R.layout.task_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findViews();
        updateUIFromItem(mTask);

        Drawable viewCalendarIcon = getResources().getDrawable(R.drawable.ic_menu_view);
        viewCalendarIcon.setBounds(0, 0, 36, 36);
        mViewCalendarButton.setCompoundDrawables(viewCalendarIcon, null, null, null);
        mViewCalendarButton.setOnClickListener(this);

        mCompleteButton.setOnClickListener(this);

        Drawable icon = getResources().getDrawable(R.drawable.ic_menu_compose_holo_light);
        icon.setBounds(0, 0, 36, 36);
        mEditButton.setCompoundDrawables(icon, null, null, null);
        mEditButton.setOnClickListener(this);
    }

    private void findViews() {
        mEditButton = (Button) getView().findViewById(R.id.edit_button);
        mCompleteButton = (Button) getView().findViewById(R.id.complete_toggle_button);
        mProjectView = (TextView) getView().findViewById(R.id.project);
        mDescriptionView = (TextView) getView().findViewById(R.id.description);
        mContextView = (LabelView) getView().findViewById(R.id.context);
        mDetailsEntry = getView().findViewById(R.id.details_entry);
        mDetailsView = (TextView) getView().findViewById(R.id.details);
        mStartView = (TextView) getView().findViewById(R.id.start);
        mDueView = (TextView) getView().findViewById(R.id.due);
        mCalendarEntry = getView().findViewById(R.id.calendar_entry);
        mViewCalendarButton = (Button) getView().findViewById(R.id.view_calendar_button);
        mStatusView = (StatusView) getView().findViewById(R.id.status);
        mCompletedView = (TextView) getView().findViewById(R.id.completed);
        mCreatedView = (TextView) getView().findViewById(R.id.created);
        mModifiedView = (TextView) getView().findViewById(R.id.modified);
    }


    private void updateUIFromItem(Task task) {
        Context context = mContextCache.findById(task.getContextId());
        Project project = mProjectCache.findById(task.getProjectId());

        updateTitle(task.getDescription());
        updateCompleteButton(task.isComplete());
        updateProject(project);
        updateDescription(task.getDescription());
        updateContext(context);
        updateDetails(task.getDetails());
        updateScheduling(task.getStartDate(), task.getDueDate());
        updateCalendar(task.getCalendarEventId());
        updateExtras(task, context, project);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.complete_toggle_button: {
                toggleComplete();
                String text = getString(R.string.itemSavedToast, getString(R.string.task_name));
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                getActivity().finish();
                break;
            }

            case R.id.view_calendar_button: {
                Uri eventUri = ContentUris.appendId(
                        CalendarUtils.getEventContentUri().buildUpon(),
                        mTask.getCalendarEventId().getId()).build();
                Intent viewCalendarEntry = new Intent(Intent.ACTION_VIEW, eventUri);
                viewCalendarEntry.putExtra(CalendarUtils.EVENT_BEGIN_TIME, mTask.getStartDate());
                viewCalendarEntry.putExtra(CalendarUtils.EVENT_END_TIME, mTask.getDueDate());
                startActivity(viewCalendarEntry);
                break;
            }

            case R.id.edit_button:
                doEditAction();
                break;
        }
    }

    protected void doEditAction() {
        Uri uri = ContentUris.appendId(
                TaskProvider.Tasks.CONTENT_URI.buildUpon(), mTask.getLocalId().getId()).build();
        Intent editIntent = new Intent(Intent.ACTION_EDIT, uri);
        startActivity(editIntent);
        getActivity().finish();
    }
    

    protected final void toggleComplete() {
        Task updatedTask = Task.newBuilder().mergeFrom(mTask)
                .setComplete(!mTask.isComplete()).build();
        mPersister.update(updatedTask);

    }
    
    private void updateTitle(String title) {
        getActivity().setTitle(title);
    }

    private void updateCompleteButton(boolean isComplete) {
        String label = getString(R.string.complete_toggle_button,
                isComplete ? getString(R.string.incomplete) : getString(R.string.complete));
        mCompleteButton.setText(label);
    }

    private void updateProject(Project project) {
        if (project == null) {
            mProjectView.setVisibility(View.GONE);
        } else {
            mProjectView.setVisibility(View.VISIBLE);
            mProjectView.setText(project.getName());
        }

    }

    private void updateDescription(String description) {
        mDescriptionView.setTextKeepState(description);
    }

    private void updateContext(Context context) {
        if (context != null) {
            mContextView.setVisibility(View.VISIBLE);
            mContextView.setText(context.getName());
            mContextView.setColourIndex(context.getColourIndex());
            ContextIcon icon = ContextIcon.createIcon(context.getIconName(), getResources());
            int id = icon.smallIconId;
            if (id > 0) {
                mContextView.setIcon(getResources().getDrawable(id));
            } else {
                mContextView.setIcon(null);
            }
        } else {
            mContextView.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDetails(String details) {
        if (TextUtils.isEmpty(details)) {
            mDetailsEntry.setVisibility(View.GONE);
        } else {
            mDetailsEntry.setVisibility(View.VISIBLE);
            mDetailsView.setText(details);
        }
    }

    private void updateScheduling(long startMillis, long dueMillis) {
        mStartView.setText(formatDateTime(startMillis));
        mDueView.setText(formatDateTime(dueMillis));
    }

    private void updateCalendar(Id calendarEntry) {
        if (calendarEntry.isInitialised()) {
            mCalendarEntry.setVisibility(View.VISIBLE);
        } else {
            mCalendarEntry.setVisibility(View.GONE);
        }
    }

    private final int cDateFormatFlags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR |
            DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH |
            DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_TIME;

    private String formatDateTime(long millis) {
        String value;
        if (millis > 0L) {
            int flags = cDateFormatFlags;
            if (DateFormat.is24HourFormat(getActivity())) {
                flags |= DateUtils.FORMAT_24HOUR;
            }
            value = DateUtils.formatDateTime(getActivity(), millis, flags);
        } else {
            value = "";
        }

        return value;
    }

    private void updateExtras(Task task, Context context, Project project) {
        mStatusView.updateStatus(task, context, project, !task.isComplete());
        mCompletedView.setText(task.isComplete() ? getString(R.string.completed) : "");
        mCreatedView.setText(formatDateTime(task.getCreatedDate()));
        mModifiedView.setText(formatDateTime(task.getModifiedDate()));
    }    
}
