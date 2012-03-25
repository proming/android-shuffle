package org.dodgybits.shuffle.android.core.model.persistence.selector;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;
import org.dodgybits.shuffle.android.core.model.Id;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.core.util.StringUtils;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;
import org.dodgybits.shuffle.android.preference.model.ListSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.dodgybits.shuffle.android.core.model.persistence.selector.Flag.*;

public class TaskSelector extends AbstractEntitySelector<TaskSelector> implements Parcelable {
    private static final String cTag = "TaskSelector";
    private static final String[] cUndefinedArgs = new String[] {};

    private ListQuery mListQuery;
    private Id mProjectId = Id.NONE;
    private Id mContextId = Id.NONE;
    private Flag mComplete = ignored;
    private Flag mPending = ignored;

    private String mSelection = null;
    private String[] mSelectionArgs = cUndefinedArgs;

    private TaskSelector() {
    }
    
    public final ListQuery getListQuery() {
        return mListQuery;
    }
    
    public final Id getProjectId() {
        return mProjectId;
    }

    public final Id getContextId() {
        return mContextId;
    }

    public final Flag getComplete() {
        return mComplete;
    }

    public final Flag getPending() {
        return mPending;
    }

    @Override
    public Uri getContentUri() {
        return TaskProvider.Tasks.CONTENT_URI;
    }

    @Override
    public final String getSelection(android.content.Context context) {
        if (mSelection == null) {
            List<String> expressions = getSelectionExpressions(context);
            mSelection = StringUtils.join(expressions, " AND ");
            Log.d(cTag, mSelection);
        }
        return mSelection;
    }

    @Override
    protected List<String> getSelectionExpressions(android.content.Context context) {
        List<String> expressions = super.getSelectionExpressions(context);
        
        if (mListQuery != null) {
            expressions.add(predefinedSelection(context));
        }
        
        addActiveExpression(expressions);
        addDeletedExpression(expressions);
        addPendingExpression(expressions);

        addIdCheckExpression(expressions, TaskProvider.Tasks.PROJECT_ID, mProjectId);
        addIdCheckExpression(expressions, TaskProvider.TaskContexts.CONTEXT_ID, mContextId);
        addFlagExpression(expressions, TaskProvider.Tasks.COMPLETE, mComplete);
        
        return expressions;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getListQuery().name());
        dest.writeLong(getContextId().getId());
        dest.writeLong(getProjectId().getId());
    }

    public static final Parcelable.Creator<TaskSelector> CREATOR
        = new Parcelable.Creator<TaskSelector>() {

        @Override
        public TaskSelector createFromParcel(Parcel source) {
            String queryName = source.readString();
            long contextId = source.readLong();
            long projectId = source.readLong();
            ListQuery query = ListQuery.valueOf(queryName);
            TaskSelector.Builder builder = TaskSelector.newBuilder().setListQuery(query);
            if (contextId != 0L) {
                builder.setContextId(Id.create(contextId));
            }
            if (projectId != 0L) {
                builder.setProjectId(Id.create(projectId));
            }

            return builder.build();
        }

        @Override
        public TaskSelector[] newArray(int size) {
            return new TaskSelector[size];
        }
    };


    private void addActiveExpression(List<String> expressions) {
        if (mActive == yes) {
            // A task is active if it is active and both project and context are active.
            String expression = "(task.active = 1 " +
            		"AND (projectId is null OR projectId IN (select p._id from project p where p.active = 1)) " +
            		//"AND (contextId is null OR contextId IN (select c._id from context c where c.active = 1)) " +
            		")";
            expressions.add(expression);
        } else if (mActive == no) {
            // task is inactive if it is inactive or project in active or context is inactive
            String expression = "(task.active = 0 " +
                "OR (projectId is not null AND projectId IN (select p._id from project p where p.active = 0)) " +
                //"OR (contextId is not null AND contextId IN (select c._id from context c where c.active = 0)) " +
                ")";
            expressions.add(expression);
        }
    }
    
    private void addDeletedExpression(List<String> expressions) {
        if (mDeleted == yes) {
            // task is deleted if it is deleted or project is deleted or context is deleted
            String expression = "(task.deleted = 1 " +
                "OR (projectId is not null AND projectId IN (select p._id from project p where p.deleted = 1)) " +
              //  "OR (contextId is not null AND contextId IN (select c._id from context c where c.deleted = 1)) " +
                ")";
            expressions.add(expression);
            
        } else if (mDeleted == no) {
            // task is not deleted if it is not deleted and project is not deleted and context is not deleted
            String expression = "(task.deleted = 0 " +
                "AND (projectId is null OR projectId IN (select p._id from project p where p.deleted = 0)) " +
            //    "AND (contextId is null OR contextId IN (select c._id from context c where c.deleted = 0)) " +
                ")";
            expressions.add(expression);
        }
    }

    private void addPendingExpression(List<String> expressions) {
        long now = System.currentTimeMillis();
        if (mPending == yes) {
            String expression = "(start > " + now + ")";
            expressions.add(expression);
        } else if (mPending == no) {
            String expression = "(start <= " + now + ")";
            expressions.add(expression);
        }
    }


    private String predefinedSelection(android.content.Context context) {
        String result;
        long now = System.currentTimeMillis();
        switch (mListQuery) {
            case nextTasks:
                result = "((complete = 0) AND " +
                    "   (start < " + now + ") AND " +
                    "   ((projectId is null) OR " +
                    "   (projectId IN (select p._id from project p where p.parallel = 1)) OR " +
                    "   (task._id = (select t2._id FROM task t2 WHERE " +
                    "      t2.projectId = task.projectId AND t2.complete = 0 AND " +
                    "      t2.deleted = 0 " +
                    "      ORDER BY displayOrder ASC limit 1))" +
                    "))";
                break;
                
            case inbox:
                result = "(projectId is null AND (select count(*) from taskContext tc where tc.taskId = task._id) = 0)";
                break;
                
            case tickler:
            case all:
            case custom:
            case context:
            case project:
                // by default show all results (completely customizable)
                result = "(1 == 1)";
                break;
                
            case dueToday:
            case dueNextWeek:
            case dueNextMonth:
                long startMS = 0L;
                long endOfToday = getEndDate();
                long endOfTomorrow = endOfToday + DateUtils.DAY_IN_MILLIS;
                result = "(due > " + startMS + ")" +
                    " AND ( (due < " + endOfToday + ") OR" +
                    "( allDay = 1 AND due < " + endOfTomorrow + " ))";
                break;

            default:
                throw new RuntimeException("Unknown predefined selection " + mListQuery);
        }
        
        return result;
    }
    
    private long getEndDate() {
        long endMS = 0L;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        switch (mListQuery) {
        case dueToday:
            cal.add(Calendar.DAY_OF_YEAR, 1);
            endMS = cal.getTimeInMillis();
            break;
        case dueNextWeek:
            cal.add(Calendar.DAY_OF_YEAR, 7);
            endMS = cal.getTimeInMillis();
            break;
        case dueNextMonth:
            cal.add(Calendar.MONTH, 1);
            endMS = cal.getTimeInMillis();
            break;
        }
        if (Log.isLoggable(cTag, Log.INFO)) {
            Log.i(cTag, "Due date ends " + endMS);
        }
        return endMS;
    }

    @Override
    public final String[] getSelectionArgs() {
        if (mSelectionArgs == cUndefinedArgs) {
            List<String> args = new ArrayList<String>();
            addIdArg(args, mProjectId);
            addIdArg(args, mContextId);

            Log.d(cTag,args.toString());
            mSelectionArgs = args.size() > 0 ? args.toArray(new String[0]): null;
        }
        return mSelectionArgs;
    }

    @Override
    public String getSortOrder() {
        String sortOrder =  super.getSortOrder();
        if (sortOrder == null && mListQuery == ListQuery.project) {
            sortOrder = TaskProvider.Tasks.DISPLAY_ORDER + " ASC";
        }
        return sortOrder;
    }

    @Override
    public Builder builderFrom() {
        return newBuilder().mergeFrom(this);
    }

    @Override
    public final String toString() {
        return String.format(
                "[TaskSelector query=%1$s project=%2$s contexts=%3$s " +
                "complete=%4$s sortOrder=%5$s active=%6$s deleted=%7$s pending=%8$s]",
                mListQuery, mProjectId, mContextId, mComplete,
                mSortOrder, mActive, mDeleted, mPending);
    }
    
    public static Builder newBuilder() {
        return Builder.create();
    }
 
    
    public static class Builder extends AbstractBuilder<TaskSelector> {

        private Builder() {
        }

        private static Builder create() {
            Builder builder = new Builder();
            builder.mResult = new TaskSelector();
            return builder;
        }
        
        public ListQuery getListQuery() {
            return mResult.mListQuery;
        }
        
        public Builder setListQuery(ListQuery value) {
            mResult.mListQuery = value;
            return this;
        }

        public Id getProjectId() {
            return mResult.mProjectId;
        }

        public Builder setProjectId(Id value) {
            mResult.mProjectId = value;
            return this;
        }
        
        public Id getContextId() {
            return mResult.mContextId;
        }

        public Builder setContextId(Id value) {
            mResult.mContextId = value;
            return this;
        }
                
        public Flag getComplete() {
            return mResult.mComplete;
        }
        
        public Builder setComplete(Flag value) {
            mResult.mComplete = value;
            return this;
        }

        public Flag getPending() {
            return mResult.mPending;
        }

        public Builder setPending(Flag value) {
            mResult.mPending = value;
            return this;
        }
        
        public Builder mergeFrom(TaskSelector query) {
            super.mergeFrom(query);

            setListQuery(query.mListQuery);
            setProjectId(query.mProjectId);
            setContextId(query.mContextId);
            setComplete(query.mComplete);
            setPending(query.mPending);

            return this;
        }

        public Builder applyListPreferences(android.content.Context context, ListSettings settings) {
            super.applyListPreferences(context, settings);

            setComplete(settings.getCompleted(context));
            setPending(settings.getPending(context));
            
            Id contextId = settings.getContextId(context);
            if (contextId.isInitialised()) {
                setContextId(contextId);
            }
            
            Id projectId = settings.getProjectId(context);
            if (projectId.isInitialised()) {
                setProjectId(projectId);
            }
            
            return this;
        }

    }

}
