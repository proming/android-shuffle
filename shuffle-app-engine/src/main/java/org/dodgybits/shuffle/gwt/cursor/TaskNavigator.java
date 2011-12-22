package org.dodgybits.shuffle.gwt.cursor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import org.dodgybits.shuffle.shared.*;

import java.util.List;

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
            if (display != mDisplay) {
                GWT.log("Ignoring range change to unknown display");
                return;
            }
            updateDisplay();
        }
      };

    private HasData<TaskProxy> mDisplay;

    private TaskQueryProxy mTaskQuery;

    private int mCurrentIndex = -1;

    private List<TaskProxy> mTasks;

    private Receiver<TaskProxy> mReceiver;

    @Inject
    public TaskNavigator(final Provider<TaskService> taskServiceProvider) {
        mTaskServiceProvider = taskServiceProvider;
    }

    public HasData<TaskProxy> getDisplay() {
        return mDisplay;
    }

    public void setDisplay(HasData<TaskProxy> display) {
        if (mDisplay != null) {
            mDataProvider.removeDataDisplay(mDisplay);
        }
        mDisplay = display;
        if (mDisplay != null) {
            mDataProvider.addDataDisplay(mDisplay);
        }
    }

    public void setTaskQuery(TaskQueryProxy query) {
        mTaskQuery = query;

        mCurrentIndex = -1;
        mTasks = null;
        mReceiver = null;
        updateDisplay();
    }

    public void requestCurrentTask(Receiver<TaskProxy> receiver) {
        TaskProxy task = getCurrentTask();
        if (task == null) {
            mReceiver = receiver;
            // modify visible range to cover current index
            int length = mDisplay.getVisibleRange().getLength();
            int newStart = (mCurrentIndex / length) * length;
            mDisplay.setVisibleRange(newStart, length);
        } else {
            receiver.onSuccess(task);
        }
    }

    public void decrementIndex() {
        mCurrentIndex--;
    }

    public void incrementIndex() {
        mCurrentIndex++;
    }

    public void setIndex(int value) {
        mCurrentIndex = value;
    }

    public void updateCurrentTask(TaskProxy task) {
        mTasks.set(mCurrentIndex - mDisplay.getVisibleRange().getStart(), task);
        mDisplay.setRowData(mDisplay.getVisibleRange().getStart(), mTasks);
    }

    private void updateDisplay() {
        if (mTaskQuery == null || mDisplay == null) {
            return;
        }

        // Send a message using RequestFactory
        final int start = mDisplay.getVisibleRange().getStart();
        final int limit = mDisplay.getVisibleRange().getLength();
        GWT.log("Loading tasks " + start + " through " + (start + limit));
        TaskService service = mTaskServiceProvider.get();

        // TODO - is this necessary?
        TaskQueryProxy query = service.create(TaskQueryProxy.class);
        query.setActive(mTaskQuery.getActive());
        query.setDeleted(mTaskQuery.getDeleted());
        query.setPredefinedQuery(mTaskQuery.getPredefinedQuery());

        Request<TaskQueryResultProxy> queryRequest = service.query(query, start, limit);
        queryRequest.fire(new Receiver<TaskQueryResultProxy>() {
            @Override
            public void onFailure(ServerFailure error) {
                GWT.log(error.getMessage());
                if (mReceiver != null) {
                    mReceiver.onFailure(error);
                    mReceiver = null;
                }
            }

            @Override
            public void onSuccess(TaskQueryResultProxy result) {
                GWT.log("Success - got " + result.getEntities().size() + " tasks");
                mDisplay.setRowData(result.getOffset(), result.getEntities());
                mDisplay.setRowCount(result.getTotalCount(), true);

                if (mCurrentIndex == -1) {
                    mCurrentIndex = start;
                }
                mTasks = result.getEntities();

                if (mReceiver != null) {
                    mReceiver.onSuccess(getCurrentTask());
                    mReceiver = null;
                }
            }
          });
    }

    private TaskProxy getCurrentTask() {
        if (mCurrentIndex == -1 || mTasks == null) {
            throw new RuntimeException("No data loaded");
        }
        if (mDisplay == null) {
            throw new RuntimeException("No display set");
        }

        TaskProxy task = null;
        int start = mDisplay.getVisibleRange().getStart();
        int absIndex = mCurrentIndex - start;
        if (absIndex >= 0 && absIndex < mTasks.size())
        {
            task = mTasks.get(absIndex);
        }
        return task;
    }


}
