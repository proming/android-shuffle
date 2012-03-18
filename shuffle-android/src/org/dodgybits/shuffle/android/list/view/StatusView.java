package org.dodgybits.shuffle.android.list.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;

public class StatusView extends TextView {

    public static enum Status {
        yes, no, fromContext, fromProject
    }


    private SpannableString mDeleted;
    private SpannableString mDeletedFromContext;
    private SpannableString mDeletedFromProject;

    private SpannableString mActive;
    private SpannableString mInactive;
    private SpannableString mInactiveFromContext;
    private SpannableString mInactiveFromProject;

    public StatusView(Context context) {
        super(context);

        createStatusStrings();
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);

        createStatusStrings();
    }

    public StatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        createStatusStrings();
    }

    private void createStatusStrings() {
        String deleted = getResources().getString(R.string.deleted);
        String active = getResources().getString(R.string.active);
        String inactive = getResources().getString(R.string.inactive);
        String fromContext =  getResources().getString(R.string.from_context);
        String fromProject =  getResources().getString(R.string.from_project);

        mDeleted = new SpannableString(deleted);
        mDeletedFromContext = new SpannableString(deleted + " " + fromContext);
        mDeletedFromProject = new SpannableString(deleted + " " + fromProject);

        mActive = new SpannableString(active);
        mInactive = new SpannableString(inactive);
        mInactiveFromContext = new SpannableString(inactive + " " + fromContext);
        mInactiveFromProject = new SpannableString(inactive + " " + fromProject);
    }

    public void updateStatus(Task task, org.dodgybits.shuffle.android.core.model.Context context, Project project, boolean showSomething) {
        updateStatus(
                activeStatus(task, context, project),
                deletedStatus(task, context, project),
                showSomething);
    }

    private Status activeStatus(Task task, org.dodgybits.shuffle.android.core.model.Context context, Project project) {
        Status status = Status.no;
        if (task.isActive()) {
            if (context != null && !context.isActive()) {
                status = Status.fromContext;
            } else if (project != null && !project.isActive()) {
                status = Status.fromProject;
            } else {
                status = Status.yes;
            }
        }
        return status;
    }

    private Status deletedStatus(Task task, org.dodgybits.shuffle.android.core.model.Context context, Project project) {
        Status status = Status.yes;
        if (!task.isDeleted()) {
            if (context != null && context.isDeleted()) {
                status = Status.fromContext;
            } else if (project != null && project.isDeleted()) {
                status = Status.fromProject;
            } else {
                status = Status.no;
            }
        }
        return status;
    }

    public void updateStatus(boolean active, boolean deleted, boolean showSomething) {
        updateStatus(
                active ? Status.yes : Status.no,
                deleted ? Status.yes : Status.no,
                showSomething);
    }

    public void updateStatus(Status active, Status deleted, boolean showSomething) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        switch (deleted) {
            case yes:
                builder.append(mDeleted);
                break;

            case fromContext:
                builder.append(mDeletedFromContext);
                break;

            case fromProject:
                builder.append(mDeletedFromProject);
                break;
        }

        builder.append(" ");

        switch (active) {
            case yes:
                if (showSomething && deleted == Status.no) builder.append(mActive);
                break;

            case no:
                builder.append(mInactive);
                break;

            case fromContext:
                builder.append(mInactiveFromContext);
                break;

            case fromProject:
                builder.append(mInactiveFromProject);
                break;
        }

        setText(builder);
        if (showSomething || builder.length() > 1) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }


}

