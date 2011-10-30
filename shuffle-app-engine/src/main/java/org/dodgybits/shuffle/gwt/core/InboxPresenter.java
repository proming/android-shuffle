package org.dodgybits.shuffle.gwt.core;

import java.util.List;

import org.dodgybits.shuffle.gwt.place.NameTokens;
import org.dodgybits.shuffle.shared.TaskProxy;
import org.dodgybits.shuffle.shared.TaskService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;

public class InboxPresenter extends
		Presenter<InboxPresenter.MyView, InboxPresenter.MyProxy> {

	public interface MyView extends View {

		void displayTasks(List<TaskProxy> tasks);
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.inbox)
	public interface MyProxy extends ProxyPlace<InboxPresenter> {
	}

	private final Provider<TaskService> taskServiceProvider;
	
	@Inject
	public InboxPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final Provider<TaskService> taskServiceProvider) {
		super(eventBus, view, proxy);
		
		this.taskServiceProvider = taskServiceProvider;
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPresenter.MAIN_SLOT, this);
	}
	
	@Override
	protected void onReset() {
		super.onReset();
		
        // Send a message using RequestFactory
        Request<List<TaskProxy>> taskListRequest = taskServiceProvider.get().listAll();
        taskListRequest.fire(new Receiver<List<TaskProxy>>() {
            @Override
            public void onFailure(ServerFailure error) {
            	GWT.log(error.getMessage());
            }

            @Override
            public void onSuccess(List<TaskProxy> tasks) {
            	GWT.log("Success - got " + tasks.size() + " tasks");
            	getView().displayTasks(tasks);
            }
          });        
		
	}
}
