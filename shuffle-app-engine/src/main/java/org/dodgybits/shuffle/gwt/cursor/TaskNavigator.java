package org.dodgybits.shuffle.gwt.cursor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import org.dodgybits.shuffle.server.model.TaskQuery;
import org.dodgybits.shuffle.shared.*;

/**
 * Keeps track of the tasks currently in view - both a list matching the current query and an individual
 * one that may be viewed or edited.
 */
public class TaskNavigator {
    private String mQueryName;

    private final Provider<TaskService> mTaskServiceProvider;

    /**
     * The provider that holds the list of contacts in the database.
     */
    private AsyncDataProvider<TaskProxy> mDataProvider = new AsyncDataProvider<TaskProxy>() {
        @Override
        protected void onRangeChanged(final HasData<TaskProxy> display) {
            updateDisplay(display);
        }
      };

    private TaskQueryProxy mTaskQuery;

    @Inject
    public TaskNavigator(final Provider<TaskService> taskServiceProvider) {
        mTaskServiceProvider = taskServiceProvider;
    }

    public void updateCursor(TaskQueryProxy query) {
        mTaskQuery = query;
    }

    private void updateDisplay(final HasData<TaskProxy> display) {
        // Send a message using RequestFactory
        final int start = display.getVisibleRange().getStart();
        final int limit = display.getVisibleRange().getLength();
        GWT.log("Loading tasks " + start + " through " + (start + limit));
        TaskService service = mTaskServiceProvider.get();
        Request<TaskQueryResultProxy> queryRequest = service.query(mTaskQuery, start, limit);
        queryRequest.fire(new Receiver<TaskQueryResultProxy>() {
            @Override
            public void onFailure(ServerFailure error) {
                GWT.log(error.getMessage());
            }

            @Override
            public void onSuccess(TaskQueryResultProxy result) {
                GWT.log("Success - got " + result.getEntities().size() + " tasks");
                display.setRowData(result.getOffset(), result.getEntities());
                display.setRowCount(result.getTotalCount(), true);
            }
          });
    }


}
