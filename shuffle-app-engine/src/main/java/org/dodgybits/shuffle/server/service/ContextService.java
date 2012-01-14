package org.dodgybits.shuffle.server.service;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.WatchedContext;

import java.util.Arrays;
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
        List<WatchedContext> contexts = q.list();
        if (contexts.isEmpty()) {
            // no contexts found - add the defaults
            createPresetContexts(contexts);
            mDao.putAll(contexts);
        }

        return contexts;
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

    private void createPresetContexts(List<WatchedContext> contexts) {
        contexts.addAll(Arrays.asList(
                createPresetContext("At home", 5, "go_home"),
                createPresetContext("At work", 19, "system_file_manager"),
                createPresetContext("Online", 1, "applications_internet"),
                createPresetContext("Errands", 14, "applications_development"),
                createPresetContext("Contact", 22, "system_users"),
                createPresetContext("Read", 16, "format_justify_fill")
        ));
    }

    private WatchedContext createPresetContext(String name, int colorIndex, String iconName) {
        WatchedContext context = new WatchedContext();
        context.setName(name);
        context.setColourIndex(colorIndex);
        context.setIconName(iconName);
        return context;
    }

}
