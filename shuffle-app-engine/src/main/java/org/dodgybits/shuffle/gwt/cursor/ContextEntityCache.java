package org.dodgybits.shuffle.gwt.cursor;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.EntityProxyChange;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import org.dodgybits.shuffle.shared.ContextProxy;
import org.dodgybits.shuffle.shared.EntityService;

import java.util.Comparator;
import java.util.List;

public class ContextEntityCache extends EntityCache<ContextProxy> {
    private final Provider<EntityService> mEntityServiceProvider;

    private final Comparator<ContextProxy> mComparator = new Comparator<ContextProxy>() {
        @Override
        public int compare(ContextProxy context1, ContextProxy context2) {
            return context1.getName().compareTo(context2.getName());
        }
    };

    @Inject
    public ContextEntityCache(
            final Provider<EntityService> entityServiceProvider,
            final EventBus eventBus)  {
        this.mEntityServiceProvider = entityServiceProvider;

        registerChangeHandler(eventBus);
    }

    private void registerChangeHandler(EventBus eventBus) {
        EntityProxyChange.registerForProxyType(eventBus, ContextProxy.class,
                new EntityProxyChange.Handler<ContextProxy>() {
                    @Override
                    public void onProxyChange(EntityProxyChange<ContextProxy> event) {
                        switch (event.getWriteOperation()) {
                            case PERSIST:
                                ContextEntityCache.this.onUpdateContext(event.getProxyId(), true);
                                break;

                            case UPDATE:
                                ContextEntityCache.this.onUpdateContext(event.getProxyId(), false);
                                break;

                            case DELETE:
                                ContextEntityCache.this.removeEntity(event.getProxyId());
                                break;
                        }
                    }
                });
    }

    @Override
    protected void fetchAll(Receiver<List<ContextProxy>> listReceiver) {
        EntityService service = mEntityServiceProvider.get();
        Request<List<ContextProxy>> request = service.fetchAllContexts();
        request.fire(listReceiver);
    }

    @Override
    protected EntityProxyId<ContextProxy> getId(ContextProxy entity) {
        return entity.stableId();
    }

    @Override
    protected Comparator<ContextProxy> getComparator() {
        return mComparator;
    }

    private void onUpdateContext(EntityProxyId<ContextProxy> proxyId, final boolean isNew) {
        mEntityServiceProvider.get().find(proxyId).fire(new Receiver<ContextProxy>() {
            @Override
            public void onSuccess(ContextProxy response) {
                if (isNew) {
                    ContextEntityCache.this.addEntity(response);
                } else {
                    ContextEntityCache.this.updateEntity(response);
                }
            }
        });
    }


}
