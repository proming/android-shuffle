package org.dodgybits.shuffle.android.list.old.config;

import org.dodgybits.shuffle.android.core.model.Task;
import org.dodgybits.shuffle.android.core.model.persistence.TaskPersister;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;

public interface TaskListConfig extends ListConfig<Task, TaskSelector> {

    TaskPersister getTaskPersister();
    
    void setTaskSelector(TaskSelector query);

    boolean showTaskContext();
    boolean showTaskProject();


}
