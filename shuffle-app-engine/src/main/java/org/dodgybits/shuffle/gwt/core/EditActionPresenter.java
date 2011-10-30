package org.dodgybits.shuffle.gwt.core;

import java.util.List;

import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.NameToken;
import org.dodgybits.shuffle.gwt.place.NameTokens;

import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import org.dodgybits.shuffle.gwt.core.MainPresenter;
import org.dodgybits.shuffle.shared.TaskProxy;
import org.dodgybits.shuffle.shared.TaskService;

public class EditActionPresenter extends
		Presenter<EditActionPresenter.MyView, EditActionPresenter.MyProxy> {

	private static final long INVALID_ID = -1;
	
	private enum Action {
		NEW, EDIT
	}
	
	public interface MyView extends View {
		// TODO Put your view methods here
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.editAction)
	public interface MyProxy extends ProxyPlace<EditActionPresenter> {
	}

	private final Provider<TaskService> taskServiceProvider;
	private PlaceManager placeManager;
	private Action mAction;
	private long mTaskId = INVALID_ID;
	
	@Inject
	public EditActionPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final PlaceManager placeManager, final Provider<TaskService> taskServiceProvider) {
		super(eventBus, view, proxy);
		this.placeManager = placeManager;
		
		this.taskServiceProvider = taskServiceProvider;
	}

	  @Override
	  public void prepareFromRequest(PlaceRequest placeRequest) {
	    super.prepareFromRequest(placeRequest);

	    // In the next call, "view" is the default value,
	    // returned if "action" is not found on the URL.
	    String actionString = placeRequest.getParameter("action", "new");
	    mAction = Action.NEW;
	    if ("edit".equals(actionString))
	    {
	    	mAction = Action.EDIT;
		    try {
		    	mTaskId = Long.valueOf(placeRequest.getParameter("taskId", null));
		    } catch(NumberFormatException e) {
		    	mTaskId = INVALID_ID;
		    }
		    
		    if (mTaskId == INVALID_ID) {
		      placeManager.revealErrorPlace(placeRequest.getNameToken());
		      return;
		    }
		    
		    loadAction();
	    }
	  }
	
	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPresenter.MAIN_SLOT, this);
	}
	
	private void loadAction()
	{
        // Send a message using RequestFactory
		//         Request<TaskProxy> taskListRequest = taskServiceProvider.get().find(new EntityProxyId<TaskProxy>() {
		// })
		//         taskListRequest.fire(new Receiver<List<TaskProxy>>() {
		//             @Override
		//             public void onFailure(ServerFailure error) {
		//             	GWT.log(error.getMessage());
		//             }
		// 
		//             @Override
		//             public void onSuccess(List<TaskProxy> tasks) {
		//             	GWT.log("Success - got " + tasks.size() + " tasks");
		//             	getView().displayTasks(tasks);
		//             }
		//           });        
	}
}
