package org.dodgybits.shuffle.gwt.core;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.dodgybits.shuffle.gwt.cursor.TaskNavigator;
import org.dodgybits.shuffle.gwt.place.NameTokens;
import org.dodgybits.shuffle.shared.*;

public class InboxPresenter extends
		Presenter<InboxPresenter.MyView, InboxPresenter.MyProxy>
    implements TaskListUiHandlers {

	public interface MyView extends View, HasUiHandlers<TaskListUiHandlers> {
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.inbox)
	public interface MyProxy extends ProxyPlace<InboxPresenter> {
	}

	private final Provider<TaskService> mTaskServiceProvider;

    private PlaceManager mPlaceManager;

    private TaskNavigator mTaskNavigator;

	@Inject
	public InboxPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final Provider<TaskService> taskServiceProvider,
            PlaceManager placeManager, TaskNavigator taskNavigator) {
		super(eventBus, view, proxy);
		
        this.mPlaceManager = placeManager;
		this.mTaskServiceProvider = taskServiceProvider;
        this.mTaskNavigator = taskNavigator;

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

    private TaskQueryProxy createQuery() {
        TaskService service = mTaskServiceProvider.get();
        TaskQueryProxy query = service.create(TaskQueryProxy.class);
        query.setActive(Flag.yes);
        query.setDeleted(Flag.no);
        query.setPredefinedQuery(PredefinedQuery.inbox);
        return query;
    }

}
