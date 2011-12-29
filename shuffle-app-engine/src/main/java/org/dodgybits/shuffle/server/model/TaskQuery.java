package org.dodgybits.shuffle.server.model;

import com.googlecode.objectify.Key;
import org.dodgybits.shuffle.shared.Flag;
import org.dodgybits.shuffle.shared.PredefinedQuery;

import java.util.List;

public class TaskQuery {

    private List<Key<Project>> mProjects;
    private List<Key<Context>> mContexts;
    private Flag mActive;
    private Flag mDeleted;
    private PredefinedQuery mPredefinedQuery;

    public List<Key<Project>> getProjects() {
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

    @Override
    public String toString() {
        return String.format("TaskQuery active=%s deleted=%s predefined=%s projects=%s contexts=%s",
                mActive, mDeleted, mPredefinedQuery, mProjects, mContexts);
    }
}
