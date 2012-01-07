package org.dodgybits.shuffle.server.service;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.WatchedContext;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextService {
    private static final Logger log = Logger.getLogger(ContextService.class.getName());

    private ObjectifyDao<WatchedContext> mDao = ObjectifyDao.newDao(WatchedContext.class);
    private TaskService mTaskService = new TaskService();

    public List<WatchedContext> fetchAll() {
        log.log(Level.FINEST, "Fetching all contexts");

        Query<WatchedContext> q = mDao.userQuery();
        q.order("name");
        return q.list();
    }

    public WatchedContext save(WatchedContext context)
    {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        context.setOwner(loggedInUser);

        if (context.getId() != null && (context.isActiveChanged() || context.isDeletedChanged())) {
            mTaskService.onContextChanged(context, mDao.key(context));
        }

        mDao.put(context);
        return context;
    }

    public void delete(WatchedContext context)
    {
        mDao.delete(context);
    }
    
    public Key<WatchedContext> getKey(WatchedContext context) {
        return mDao.key(context);
    }

}
