package org.dodgybits.shuffle.server.service;

import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.TaskQuery;
import org.dodgybits.shuffle.shared.PredefinedQuery;

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
        taskQuery = query.get();
        if (taskQuery == null) {
            taskQuery = createDefaultQuery(name);
        }
        return taskQuery;
    }

    private TaskQuery createDefaultQuery(String name) {
        TaskQuery query = new TaskQuery();
        if ("tickler".equals(name)) {
            query.setPredefinedQuery(PredefinedQuery.all);
        } else if ("nextActions".equals(name)) {
            query.setPredefinedQuery(PredefinedQuery.nextTasks);
        } else {
            if (!"inbox".equals(name)) {
                log.log(Level.WARNING, "Unknown queryName %s defaulting to inbox", name);
            }
            query.setPredefinedQuery(PredefinedQuery.inbox);
        }

        return query;
    }
}
