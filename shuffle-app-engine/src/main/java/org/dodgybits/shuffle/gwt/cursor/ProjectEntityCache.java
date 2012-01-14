package org.dodgybits.shuffle.gwt.cursor;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.EntityProxyChange;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import org.dodgybits.shuffle.shared.EntityService;
import org.dodgybits.shuffle.shared.ProjectProxy;

import java.util.Comparator;
import java.util.List;

public class ProjectEntityCache extends EntityCache<ProjectProxy> {
    private final Provider<EntityService> mEntityServiceProvider;

    private final Comparator<ProjectProxy> mComparator = new Comparator<ProjectProxy>() {
        @Override
        public int compare(ProjectProxy project1, ProjectProxy project2) {
            return project1.getName().compareTo(project2.getName());
        }
    };

    @Inject
    public ProjectEntityCache(
            final Provider<EntityService> entityServiceProvider,
            final EventBus eventBus)  {
        this.mEntityServiceProvider = entityServiceProvider;

        registerChangeHandler(eventBus);
    }

    private void registerChangeHandler(EventBus eventBus) {
        EntityProxyChange.registerForProxyType(eventBus, ProjectProxy.class,
                new EntityProxyChange.Handler<ProjectProxy>() {
                    @Override
                    public void onProxyChange(EntityProxyChange<ProjectProxy> event) {
                        switch (event.getWriteOperation()) {
                            case PERSIST:
                                ProjectEntityCache.this.onUpdateProject(event.getProxyId(), true);
                                break;

                            case UPDATE:
                                ProjectEntityCache.this.onUpdateProject(event.getProxyId(), false);
                                break;

                            case DELETE:
                                ProjectEntityCache.this.removeEntity(event.getProxyId());
                                break;
                        }
                    }
                });
    }

    @Override
    protected void fetchAll(Receiver<List<ProjectProxy>> listReceiver) {
        EntityService service = mEntityServiceProvider.get();
        Request<List<ProjectProxy>> request = service.fetchAllProjects();
        request.fire(listReceiver);
    }

    @Override
    protected EntityProxyId<ProjectProxy> getId(ProjectProxy entity) {
        return entity.stableId();
    }

    @Override
    protected Comparator<ProjectProxy> getComparator() {
        return mComparator;
    }

    private void onUpdateProject(EntityProxyId<ProjectProxy> proxyId, final boolean isNew) {
        mEntityServiceProvider.get().find(proxyId).fire(new Receiver<ProjectProxy>() {
            @Override
            public void onSuccess(ProjectProxy response) {
                if (isNew) {
                    ProjectEntityCache.this.addEntity(response);
                } else {
                    ProjectEntityCache.this.updateEntity(response);
                }
            }
        });
    }
}
