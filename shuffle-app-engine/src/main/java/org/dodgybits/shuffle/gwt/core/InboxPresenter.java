package org.dodgybits.shuffle.gwt.core;

import java.util.List;

import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
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
		Presenter<InboxPresenter.MyView, InboxPresenter.MyProxy>
    implements TaskListUiHandlers {

	public interface MyView extends View, HasUiHandlers<TaskListUiHandlers> {

		void displayTasks(List<TaskProxy> tasks);
        void redraw();
	}

	@ProxyCodeSplit
	@NameToken(NameTokens.inbox)
	public interface MyProxy extends ProxyPlace<InboxPresenter> {
	}

	private final Provider<TaskService> taskServiceProvider;

    private PlaceManager placeManager;

    private List<TaskProxy> mTasks;

    private Long mEditedTaskId = null;

	@Inject
	public InboxPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final Provider<TaskService> taskServiceProvider, PlaceManager placeManager) {
		super(eventBus, view, proxy);
		
        this.placeManager = placeManager;
		this.taskServiceProvider = taskServiceProvider;

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

        if (mTasks == null) {
            loadTasks();
        } else if (mEditedTaskId != null) {
            updateEditedTask();
        }


    }

    @Override
    public void onEditAction(TaskProxy proxy) {
        mEditedTaskId = proxy.getId();
        PlaceRequest myRequest = new PlaceRequest(NameTokens.editAction)
                .with("action", "edit")
                .with("taskId", String.valueOf(mEditedTaskId));
        placeManager.revealPlace( myRequest );
    }

    private void loadTasks() {
        // Send a message using RequestFactory
        Request<List<TaskProxy>> taskListRequest = taskServiceProvider.get().listAll();
        taskListRequest.fire(new Receiver<List<TaskProxy>>() {
            @Override
            public void onFailure(ServerFailure error) {
                GWT.log(error.getMessage());
            }

            @Override
            public void onSuccess(List<TaskProxy> tasks) {
                mTasks = tasks;
                GWT.log("Success - got " + tasks.size() + " tasks");
                getView().displayTasks(mTasks);
            }
          });
    }

    private void updateEditedTask() {
        GWT.log("Update task that was just edited " + mEditedTaskId);
        Request<TaskProxy> taskListRequest = taskServiceProvider.get().findById(mEditedTaskId);
        taskListRequest.fire(new Receiver<TaskProxy>() {
            @Override
            public void onFailure(ServerFailure error) {
                GWT.log(error.getMessage());
            }

            @Override
            public void onSuccess(TaskProxy task) {
                boolean found = false;
                int i;
                for (i = 0; i < mTasks.size(); i++) {
                    TaskProxy currentTask =  mTasks.get(i);
                    GWT.log("Checking task " + currentTask.getId());

                    if (currentTask.getId() == mEditedTaskId)
                    {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    GWT.log("Replace with edited task " + task.getDetails() + " and update view");
                    mTasks.set(i, task);
                    getView().redraw();
                }
            }
          });
    }

}
