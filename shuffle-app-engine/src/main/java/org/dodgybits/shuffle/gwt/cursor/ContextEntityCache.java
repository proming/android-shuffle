package org.dodgybits.shuffle.gwt.cursor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import org.dodgybits.shuffle.shared.ContextProxy;
import org.dodgybits.shuffle.shared.ContextService;

import java.util.List;

public class ContextEntityCache extends EntityCache<ContextProxy> {
    private final Provider<ContextService> mContextServiceProvider;

    @Inject
    public ContextEntityCache(final Provider<ContextService> contextServiceProvider) {
        this.mContextServiceProvider = contextServiceProvider;
    }

    @Override
    protected void fetchAll(Receiver<List<ContextProxy>> listReceiver) {
        ContextService service = mContextServiceProvider.get();
        Request<List<ContextProxy>> request = service.fetchAll();
        request.fire(listReceiver);
    }

    @Override
    protected Long getId(ContextProxy entity) {
        return entity.getId();
    }
}
