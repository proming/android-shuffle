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
import org.dodgybits.shuffle.android.core.util.TaskLifecycleState;
import org.dodgybits.shuffle.android.core.util.TaskLifecycleState.Status;

import java.util.List;

public class StatusView extends TextView {

    private SpannableString mDeleted;
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
        mDeletedFromProject = new SpannableString(deleted + " " + fromProject);

        mActive = new SpannableString(active);
        mInactive = new SpannableString(inactive);
        mInactiveFromContext = new SpannableString(inactive + " " + fromContext);
        mInactiveFromProject = new SpannableString(inactive + " " + fromProject);
    }

    public void updateStatus(Task task, List<Context> contexts, Project project, boolean showSomething) {
        updateStatus(
                TaskLifecycleState.getActiveStatus(task, contexts, project),
                TaskLifecycleState.getDeletedStatus(task, project),
                showSomething);
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

