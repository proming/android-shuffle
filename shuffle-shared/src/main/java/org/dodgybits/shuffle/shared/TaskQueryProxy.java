package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName("org.dodgybits.shuffle.server.model.TaskQuery")
public interface TaskQueryProxy extends ValueProxy {

    Flag getActive();
    void setActive(Flag value);

    Flag getDeleted();
    void setDeleted(Flag value);

    /**
     * @return maximum number of results requested
     */
    int getCount();
    void setCount(int value);

    /**
     * @return offset from the start when retrieving results
     */
    int getOffset();
    void setOffset(int value);

    PredefinedQuery getPredefinedQuery();
    void setPredefinedQuery(PredefinedQuery query);

}
