package org.dodgybits.shuffle.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

import java.util.List;

@ProxyForName("org.dodgybits.shuffle.server.model.TaskQueryResult")
public interface TaskQueryResultProxy extends ValueProxy {

    List<TaskProxy> getEntities();

    /**
     * @return total number of entities matching the query
     */
    int getTotalCount();

    /**
     * @return offset from the start of the current result list
     */
    int getOffset();

}
