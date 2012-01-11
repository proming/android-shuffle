package org.dodgybits.shuffle.gwt.cursor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.*;
import org.dodgybits.shuffle.shared.*;

import java.util.Collections;
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

    private final EventBus mEventBus;

    private HasData<TaskProxy> mDisplay;

    private TaskQueryProxy mTaskQuery;

    private int mCurrentIndex = -1;

    private List<TaskProxy> mTasks;

    private Receiver<TaskProxy> mReceiver;

    @Inject
    public TaskNavigator(final EventBus eventBus, final Provider<TaskService> taskServiceProvider) {
        mTaskServiceProvider = taskServiceProvider;
        mEventBus = eventBus;

        EntityProxyChange.registerForProxyType(eventBus, TaskProxy.class,
                new EntityProxyChange.Handler<TaskProxy>() {
                    @Override
                    public void onProxyChange(EntityProxyChange<TaskProxy> event) {
                        TaskNavigator.this.onTaskChanged(event);
                    }
                });

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
        setCurrentIndex(mCurrentIndex - 1);
    }

    public void incrementIndex() {
        setCurrentIndex(mCurrentIndex + 1);
    }

    public void setCurrentIndex(int value) {
        value = Math.max(0, value);
        if (mDisplay != null && mDisplay.isRowCountExact()) {
            value = Math.min(mDisplay.getRowCount() - 1, value);
        }
        mCurrentIndex = value;
    }

    private void onTaskChanged(EntityProxyChange<TaskProxy> event) {
        EntityProxyId<TaskProxy> taskId = event.getProxyId();
        switch (event.getWriteOperation()) {
            case PERSIST:
                GWT.log("New task " + taskId);
                updateDisplay();
                break;

            case UPDATE:
                GWT.log("Received update for task " + taskId);

                // Is the changing record onscreen?
                int displayOffset = offsetOf(taskId);
                if (displayOffset != -1) {
                    // Record is onscreen and may differ from our data
                    mTaskServiceProvider.get().find(taskId).fire(new Receiver<TaskProxy>() {
                        @Override
                        public void onSuccess(TaskProxy task) {
                            // Re-check offset in case of changes while waiting for data
                            int offset = offsetOf(task.stableId());
                            if (offset != -1 && mDisplay != null) {
                                mDisplay.setRowData(mDisplay.getVisibleRange().getStart() + offset,
                                        Collections.singletonList(task));
                            }
                        }
                    });
                }
                break;

            case DELETE:
                GWT.log("Task deleted " + taskId);
                break;
        }
    }

    private int offsetOf(EntityProxyId<TaskProxy> taskId) {
        if (mTasks != null) {
            for (int offset = 0, j = mTasks.size(); offset < j; offset++) {
                if (taskId.equals(mTasks.get(offset).stableId())) {
                    return offset;
                }
            }
        }
        return -1;
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

        Request<TaskQueryResultProxy> queryRequest = service.query(query, start, limit).
                with("entities.contexts").with("entities.project");
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
