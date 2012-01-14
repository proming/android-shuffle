package org.dodgybits.shuffle.gwt.core.tasklist;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.dodgybits.shuffle.gwt.core.MainPresenter;
import org.dodgybits.shuffle.gwt.cursor.TaskNavigator;
import org.dodgybits.shuffle.gwt.place.NameTokens;
import org.dodgybits.shuffle.shared.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskListPresenter extends
		Presenter<TaskListPresenter.MyView, TaskListPresenter.MyProxy>
    implements TaskListUiHandlers {

    private static final Logger log = Logger.getLogger(TaskListPresenter.class.getName());

    @ProxyCodeSplit
    @NameToken(NameTokens.taskList)
    public interface MyProxy extends ProxyPlace<TaskListPresenter> {
    }

	public interface MyView extends View, HasUiHandlers<TaskListUiHandlers> {
        void redraw();
	}

	protected final Provider<EntityService> mEntityServiceProvider;
    private final PlaceManager mPlaceManager;
    private final TaskNavigator mTaskNavigator;
    String mQueryName;
    
	@Inject
	public TaskListPresenter(final EventBus eventBus, final MyView view,
                             final TaskListPresenter.MyProxy proxy,
                             final Provider<EntityService> entityServiceProvider,
                             PlaceManager placeManager, TaskNavigator taskNavigator) {
		super(eventBus, view, proxy);
		
        mPlaceManager = placeManager;
        mEntityServiceProvider = entityServiceProvider;
        mTaskNavigator = taskNavigator;

        getView().setUiHandlers(this);
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPresenter.MAIN_SLOT, this);
	}

    @Override
    public void prepareFromRequest(final PlaceRequest placeRequest) {
        super.prepareFromRequest(placeRequest);

        mQueryName = placeRequest.getParameter("q", "inbox");
        mTaskNavigator.setTaskQueryName(mQueryName);
    }

    @Override
    public void onEditAction(int index, TaskProxy task) {
        mTaskNavigator.setCurrentIndex(index);
        RequestFactory factory = mEntityServiceProvider.get().getRequestFactory();
        String idToken = factory.getHistoryToken(task.stableId());
        PlaceRequest myRequest = new PlaceRequest(NameTokens.editAction)
                .with("action", "edit")
                .with("id", idToken);
        mPlaceManager.revealPlace(myRequest);
    }

    @Override
    public void setDisplay(HasData<TaskProxy> view) {
        mTaskNavigator.setDisplay(view);
    }

}
