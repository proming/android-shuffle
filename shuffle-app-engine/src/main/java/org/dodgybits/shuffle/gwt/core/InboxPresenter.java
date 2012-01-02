package org.dodgybits.shuffle.gwt.core;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.dodgybits.shuffle.gwt.cursor.ContextEntityCache;
import org.dodgybits.shuffle.gwt.cursor.ProjectEntityCache;
import org.dodgybits.shuffle.gwt.cursor.TaskNavigator;
import org.dodgybits.shuffle.gwt.place.NameTokens;
import org.dodgybits.shuffle.shared.*;

import javax.annotation.Nullable;
import java.util.List;

public class InboxPresenter extends
		Presenter<InboxPresenter.MyView, InboxPresenter.MyProxy>
    implements TaskListUiHandlers {

	public interface MyView extends View, HasUiHandlers<TaskListUiHandlers> {
        void redraw();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.inbox)
	public interface MyProxy extends ProxyPlace<InboxPresenter> {
	}

	private final Provider<TaskService> mTaskServiceProvider;

    private final PlaceManager mPlaceManager;
    private final TaskNavigator mTaskNavigator;
    private final ContextEntityCache mContextCache;
    private final ProjectEntityCache mProjectCache;


	@Inject
	public InboxPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final Provider<TaskService> taskServiceProvider,
            final ContextEntityCache contextCache,
            final ProjectEntityCache projectCache,
            PlaceManager placeManager, TaskNavigator taskNavigator) {
		super(eventBus, view, proxy);
		
        mPlaceManager = placeManager;
		mTaskServiceProvider = taskServiceProvider;
        mTaskNavigator = taskNavigator;
        mContextCache = contextCache;
        mProjectCache = projectCache;

        getView().setUiHandlers(this);
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPresenter.MAIN_SLOT, this);
	}

    @Override
    protected void onReveal() {
        super.onReveal();

        GWT.log("InboxPresenter onReveal()");

        mTaskNavigator.setTaskQuery(createQuery());
        requestContexts();
        requestProjects();
    }

    @Override
    public void onEditAction(int index, TaskProxy proxy) {
        mTaskNavigator.setCurrentIndex(index);
        PlaceRequest myRequest = new PlaceRequest(NameTokens.editAction)
                .with("action", "edit");
        mPlaceManager.revealPlace( myRequest );
    }

    @Override
    public void setDisplay(HasData<TaskProxy> view) {
        mTaskNavigator.setDisplay(view);
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

    private TaskQueryProxy createQuery() {
        TaskService service = mTaskServiceProvider.get();
        TaskQueryProxy query = service.create(TaskQueryProxy.class);
        query.setActive(Flag.yes);
        query.setDeleted(Flag.no);
        query.setPredefinedQuery(PredefinedQuery.inbox);
        return query;
    }
    
    private void requestProjects() {
        mProjectCache.requestEntities(new Receiver<List<ProjectProxy>>() {
            @Override
            public void onSuccess(List<ProjectProxy> response) {
                // notify view
                getView().redraw();
            }
        });
    }
    
    private void requestContexts() {
        mContextCache.requestEntities(new Receiver<List<ContextProxy>>() {
            @Override
            public void onSuccess(List<ContextProxy> response) {
                // notify view
                getView().redraw();
            }
        });
    }

}
