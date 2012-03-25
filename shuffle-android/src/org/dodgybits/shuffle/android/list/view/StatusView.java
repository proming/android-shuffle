package org.dodgybits.shuffle.android.list.view;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.Context;
import org.dodgybits.shuffle.android.core.model.Project;
import org.dodgybits.shuffle.android.core.model.Task;

import java.util.List;

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

    public StatusView(android.content.Context context) {
        super(context);

        createStatusStrings();
    }

    public StatusView(android.content.Context context, AttributeSet attrs) {
        super(context, attrs);

        createStatusStrings();
    }

    public StatusView(android.content.Context context, AttributeSet attrs, int defStyle) {
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

    public void updateStatus(Task task, List<Context> contexts, Project project, boolean showSomething) {
        updateStatus(
                activeStatus(task, contexts, project),
                deletedStatus(task, contexts, project),
                showSomething);
    }

    private Status activeStatus(Task task, List<Context> contexts, Project project) {
        Status status;
        if (task.isActive()) {
            status = Status.yes;
            if (project != null && !project.isActive()) {
                status = Status.fromProject;
            } else if (!contexts.isEmpty()) {
                // task is inactive if all contexts are inactive
                boolean foundActive = false;
                for (Context context : contexts) {
                    if (context.isActive()) {
                        foundActive = true;
                        break;
                    }
                }
                if (!foundActive) {
                    status = Status.fromContext;
                }
            }
        } else {
            status = Status.no;
        }
        return status;
    }

    private Status deletedStatus(Task task, List<Context> contexts, Project project) {
        Status status;
        if (task.isDeleted()) {
            status = Status.yes;
        } else {
            status = Status.no;
            if (project != null && project.isDeleted()) {
                status = Status.fromProject;
            } else if (!contexts.isEmpty()) {
                // task is deleted if all contexts are deleted
                boolean foundNotDeleted = false;
                for (Context context : contexts) {
                    if (!context.isDeleted()) {
                        foundNotDeleted = true;
                        break;
                    }
                }
                if (!foundNotDeleted) {
                    status = Status.fromContext;
                }
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

