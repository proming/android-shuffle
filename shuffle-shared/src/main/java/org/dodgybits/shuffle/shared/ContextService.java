package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

import java.util.List;

@ServiceName(value = "org.dodgybits.shuffle.server.service.ContextService", 
        locator="org.dodgybits.shuffle.server.locator.DaoServiceLocator")
public interface ContextService extends RequestContext {

    Request<ContextProxy> save(ContextProxy newContext);

    Request<Void> delete(ContextProxy context);

    Request<List<ContextProxy>> fetchAll();

}
