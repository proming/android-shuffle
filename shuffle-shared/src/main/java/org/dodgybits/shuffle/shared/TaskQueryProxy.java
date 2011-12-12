package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName("org.dodgybits.shuffle.server.model.TaskQuery")
public interface TaskQueryProxy extends ValueProxy {

    Flag getActive();
    void setActive(Flag value);

    Flag getDeleted();
    void setDeleted(Flag value);

    PredefinedQuery getPredefinedQuery();
    void setPredefinedQuery(PredefinedQuery query);

}
