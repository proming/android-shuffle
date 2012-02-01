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
import roboguice.inject.InjectView;

public class TaskViewFragment extends RoboFragment implements View.OnClickListener {
    public static final String SELECTED_INDEX = "selectedIndex";
    public static final String TASK_LIST_CONTEXT = "taskListContext";


    public static final String INDEX = "TaskViewFragment.index";
    public static final String COUNT = "TaskViewFragment.count";

    private @Inject
    TaskEncoder mEncoder;
    
    private @InjectView(R.id.edit_button) Button mEditButton;
    
    private @InjectView(R.id.complete_toggle_button)
    Button mCompleteButton;

    private @InjectView(R.id.project)
    TextView mProjectView;
    private @InjectView(R.id.description) TextView mDescriptionView;
    private @InjectView(R.id.context)
    LabelView mContextView;

    private @InjectView(R.id.details_entry)
    View mDetailsEntry;
    private @InjectView(R.id.details) TextView mDetailsView;

    private @InjectView(R.id.start) TextView mStartView;
    private @InjectView(R.id.due) TextView mDueView;

    private @InjectView(R.id.calendar_entry) View mCalendarEntry;
    private @InjectView(R.id.view_calendar_button) Button mViewCalendarButton;

    private @InjectView(R.id.status)
    StatusView mStatusView;
    private @InjectView(R.id.completed) TextView mCompletedView;
    private @InjectView(R.id.created) TextView mCreatedView;
    private @InjectView(R.id.modified) TextView mModifiedView;

    @Inject
    private EntityCache<Project> mProjectCache;
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

        mTask = mEncoder.restore(getArguments());
        mTaskCount = getArguments().getInt(COUNT);
        mPosition = getArguments().getInt(INDEX);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.task_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    private void updateUIFromItem(Task task) {
        Context context = mContextCache.findById(task.getContextId());
        Project project = mProjectCache.findById(task.getProjectId());

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
