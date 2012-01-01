package org.dodgybits.shuffle.gwt.cursor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import org.dodgybits.shuffle.shared.ProjectProxy;
import org.dodgybits.shuffle.shared.ProjectService;

import java.util.List;

public class ProjectEntityCache extends EntityCache<ProjectProxy> {
    private final Provider<ProjectService> mProjectServiceProvider;

    @Inject
    public ProjectEntityCache(final Provider<ProjectService> projectServiceProvider) {
        this.mProjectServiceProvider = projectServiceProvider;
    }

    @Override
    protected void fetchAll(Receiver<List<ProjectProxy>> listReceiver) {
        ProjectService service = mProjectServiceProvider.get();
        Request<List<ProjectProxy>> request = service.fetchAll();
        request.fire(listReceiver);
    }

    @Override
    protected Long getId(ProjectProxy entity) {
        return entity.getId();
    }
}
