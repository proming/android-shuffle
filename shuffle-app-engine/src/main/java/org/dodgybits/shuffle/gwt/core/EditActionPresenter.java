package org.dodgybits.shuffle.gwt.core;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import org.dodgybits.shuffle.gwt.cursor.ContextEntityCache;
import org.dodgybits.shuffle.gwt.cursor.ProjectEntityCache;
import org.dodgybits.shuffle.gwt.cursor.TaskNavigator;
import org.dodgybits.shuffle.gwt.place.NameTokens;

import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.dodgybits.shuffle.shared.ContextProxy;
import org.dodgybits.shuffle.shared.ProjectProxy;
import org.dodgybits.shuffle.shared.TaskProxy;
import org.dodgybits.shuffle.shared.TaskService;

import javax.annotation.Nullable;
import java.util.List;

public class EditActionPresenter extends
        Presenter<EditActionPresenter.MyView, EditActionPresenter.MyProxy>
        implements EditEntityUiHandlers {


    private enum Action {
        NEW, EDIT
    }

    public interface MyView extends View, HasUiHandlers<EditEntityUiHandlers> {
        void displayTask(TaskProxy task);
        void displayProject(ProjectProxy project);
        void displayContexts(List<ContextProxy> contexts);
    }

    @ProxyCodeSplit
    @NameToken(NameTokens.editAction)
    public interface MyProxy extends ProxyPlace<EditActionPresenter> {
    }

    private final Provider<TaskService> mTaskServiceProvider;
    private final PlaceManager mPlaceManager;
    private final TaskNavigator mTaskNavigator;
    private final ContextEntityCache mContextCache;
    private final ProjectEntityCache mProjectCache;
    private Action mAction;
    private TaskProxy mTask = null;

    @Inject
    public EditActionPresenter(
            final EventBus eventBus, final MyView view,
            final MyProxy proxy, final PlaceManager placeManager,
            final Provider<TaskService> taskServiceProvider,
            final ContextEntityCache contextCache,
            final ProjectEntityCache projectCache,
            final TaskNavigator taskNavigator) {
        super(eventBus, view, proxy);
        mPlaceManager = placeManager;
        mTaskServiceProvider = taskServiceProvider;
        mTaskNavigator = taskNavigator;
        mContextCache = contextCache;
        mProjectCache = projectCache;

        getView().setUiHandlers(this);
    }

    @Override
    public void prepareFromRequest(final PlaceRequest placeRequest) {
        super.prepareFromRequest(placeRequest);

        // In the next call, "view" is the default value,
        // returned if "action" is not found on the URL.
        String actionString = placeRequest.getParameter("action", "new");
        mAction = Action.NEW;
        if ("edit".equals(actionString)) {
            mAction = Action.EDIT;
            fetchTask();
            requestContexts();
            requestProjects();
        }
    }

    @Override
    protected void revealInParent() {
        RevealContentEvent.fire(this, MainPresenter.MAIN_SLOT, this);
    }

    @Override
    public void save(String description, String details) {
        TaskService service = mTaskServiceProvider.get();
        if (mAction == Action.NEW) {
            mTask = service.create(TaskProxy.class);
        } else {
            mTask = service.edit(mTask);
        }

        mTask.setDescription(description);
        mTask.setDetails(details);

        Request<TaskProxy> saveRequest = service.save(mTask);
        saveRequest.fire(new Receiver<TaskProxy>() {
            @Override
            public void onFailure(ServerFailure error) {
                GWT.log(error.getMessage());
            }

            @Override
            public void onSuccess(TaskProxy response) {
                GWT.log("Success");
                // TODO update task via navigator
                mTaskNavigator.updateCurrentTask(mTask);
                goBack();
            }
        });
    }

    @Override
    public void cancel() {
        goBack();
    }

    @Override
    public void next() {
        mTaskNavigator.incrementIndex();
        fetchTask();
    }

    @Override
    public void previous() {
        mTaskNavigator.decrementIndex();
        fetchTask();
    }

    private void fetchTask() {
        mTaskNavigator.requestCurrentTask(new Receiver<TaskProxy>() {
            @Override
            public void onSuccess(TaskProxy task) {
                mTask = task;
                GWT.log("Success - got " + task);
                getView().displayTask(task);

            }

        });
    }

    private void goBack() {
        mPlaceManager.navigateBack();
    }

    @Override
    public List<ContextProxy> getContexts(TaskProxy task) {
        return Lists.transform(task.getContextIds(), new Function<Long, ContextProxy>() {
            @Override
            public ContextProxy apply(@Nullable Long input) {
                return mContextCache.findById(input);
            }
        });
    }

    @Override
    public ProjectProxy getProject(TaskProxy task) {
        return mProjectCache.findById(task.getProjectId());
    }

    private void requestProjects() {
        mProjectCache.requestEntities(new Receiver<List<ProjectProxy>>() {
            @Override
            public void onSuccess(List<ProjectProxy> response) {
                if (mTask != null) {
                    ProjectProxy project = getProject(mTask);
                    getView().displayProject(project);
                }
            }
        });
    }

    private void requestContexts() {
        mContextCache.requestEntities(new Receiver<List<ContextProxy>>() {
            @Override
            public void onSuccess(List<ContextProxy> response) {
                if (mTask != null) {
                    List<ContextProxy> contexts = getContexts(mTask);
                    getView().displayContexts(contexts);
                }
            }
        });
    }

}
