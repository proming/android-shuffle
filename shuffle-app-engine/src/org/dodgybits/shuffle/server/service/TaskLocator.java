package org.dodgybits.shuffle.server.service;

import org.dodgybits.shuffle.server.model.Task;

import com.google.web.bindery.requestfactory.shared.Locator;

public class TaskLocator extends Locator<Task,Long> {

    @Override
    public Task create(Class<? extends Task> clazz) {
        return TaskService.createTask();
    }

    @Override
    public Task find(Class<? extends Task> clazz, Long id) {
        return TaskService.readTask(id);
    }

    @Override
    public Class<Task> getDomainType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getId(Task domainObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<Long> getIdType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getVersion(Task domainObject) {
        throw new UnsupportedOperationException();
    }

    
}
