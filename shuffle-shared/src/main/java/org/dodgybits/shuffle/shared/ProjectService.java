package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import java.util.List;

@ServiceName(value = "org.dodgybits.shuffle.server.service.ProjectService", 
        locator="org.dodgybits.shuffle.server.locator.DaoServiceLocator")
public interface ProjectService extends RequestContext {

    Request<ProjectProxy> save(ProjectProxy newProject);

    Request<Void> delete(ProjectProxy project);

    Request<List<ProjectProxy>> fetchAll();

}
