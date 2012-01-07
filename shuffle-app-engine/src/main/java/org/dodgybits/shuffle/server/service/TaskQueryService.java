package org.dodgybits.shuffle.server.service;

import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.TaskQuery;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskQueryService {
    private static final Logger log = Logger.getLogger(TaskQueryService.class.getName());

    private ObjectifyDao<TaskQuery> mTaskQueryDao = ObjectifyDao.newDao(TaskQuery.class);

    public TaskQuery save(TaskQuery query) {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        query.setOwner(loggedInUser);
        mTaskQueryDao.put(query);
        return query;
    }

    public TaskQuery findQueryByName(String name) {
        TaskQuery taskQuery = null;
        Query<TaskQuery> query = mTaskQueryDao.userQuery();
        query.filter("name", name);
        log.log(Level.FINE, "Looking up task query {0}", name);
        List<TaskQuery> queries = query.list();
        if (queries.size() == 1) {
            taskQuery = queries.get(0);
        } else if (queries.size() > 1) {
            throw new IllegalStateException("More than 1 query found for name " + name);
        }
        return taskQuery;
    }


}
