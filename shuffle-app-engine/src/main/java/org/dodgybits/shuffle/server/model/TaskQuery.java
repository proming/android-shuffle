package org.dodgybits.shuffle.server.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import org.dodgybits.shuffle.shared.Flag;
import org.dodgybits.shuffle.shared.PredefinedQuery;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

public class TaskQuery extends UserDatastoreObject {

    private String mName;
    private List<Key<Project>> mProjects = Lists.newArrayList();
    private List<Key<Context>> mContexts = Lists.newArrayList();
    private Flag mActive;
    private Flag mDeleted;
    private Date mDueDateFrom;
    private Date mDueDateTo;
    private PredefinedQuery mPredefinedQuery;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<Key<Project>> getProjectKeys() {
        return mProjects;
    }

    public void setProjects(List<Key<Project>> projects) {
        mProjects = projects;
    }

    public List<Key<Context>> getContexts() {
        return mContexts;
    }

    public void setContexts(List<Key<Context>> contexts) {
        mContexts = contexts;
    }

    public List<Long> getProjectIds() {
        return Lists.transform(mProjects, new Function<Key<Project>, Long>() {
            @Override
            public Long apply(@Nullable Key<Project> input) {
                return input.getId();
            }
        });
    }
    
    public void setProjectIds(List<Long> projectIds) {
        mProjects = Lists.newArrayList(Lists.transform(projectIds, new Function<Long, Key<Project>>() {
            @Override
            public Key<Project> apply(@Nullable Long input) {
                return new Key<Project>(Project.class, input);
            }
        }));
    }

    public List<Long> getContextIds() {
        return Lists.transform(mContexts, new Function<Key<Context>, Long>() {
            @Override
            public Long apply(@Nullable Key<Context> input) {
                return input.getId();
            }
        });
    }

    public void setContextIds(List<Long> contextIds) {
        mContexts = Lists.newArrayList(Lists.transform(contextIds, new Function<Long, Key<Context>>() {
            @Override
            public Key<Context> apply(@Nullable Long input) {
                return new Key<Context>(Context.class, input);
            }
        }));
    }
    
    public Flag getActive() {
        return mActive;
    }

    public void setActive(Flag value) {
        mActive = value;
    }

    public Flag getDeleted() {
        return mDeleted;
    }
    public void setDeleted(Flag value) {
        mDeleted = value;
    }

    public PredefinedQuery getPredefinedQuery() {
        return mPredefinedQuery;
    }

    public void setPredefinedQuery(PredefinedQuery predefinedQuery) {
        mPredefinedQuery = predefinedQuery;
    }

    public Date getDueDateFrom() {
        return mDueDateFrom;
    }

    public void setDueDateFrom(Date dueDateFrom) {
        mDueDateFrom = dueDateFrom;
    }

    public Date getDueDateTo() {
        return mDueDateTo;
    }

    public void setDueDateTo(Date dueDateTo) {
        mDueDateTo = dueDateTo;
    }

    @Override
    public String toString() {
        return String.format(
                "TaskQuery active=%s deleted=%s predefined=%s projects=%s contexts=%s due %d-%d",
                mActive, mDeleted, mPredefinedQuery, mProjects, mContexts, mDueDateFrom, mDueDateTo);
    }
}
