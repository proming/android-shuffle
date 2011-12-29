package org.dodgybits.shuffle.server.service;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.Context;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextService {
    private static final Logger log = Logger.getLogger(ContextService.class.getName());

    private ContextDao mDao = new ContextDao();

    public List<Context> fetchAll() {
        log.log(Level.FINEST, "Fetching all contexts");

        Query<Context> q = mDao.userQuery();
        q.order("name");
        return q.list();
    }

    public Context save(Context context)
    {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        context.setOwner(loggedInUser);
        mDao.put(context);
        return context;
    }

    public void delete(Context context)
    {
        mDao.delete(context);
    }
    
    public Key<Context> getKey(Context context) {
        return mDao.key(context);
    }

}
