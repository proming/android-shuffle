package org.dodgybits.shuffle.server.service;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectService {
    private static final Logger log = Logger.getLogger(ProjectService.class.getName());

    private ObjectifyDao<WatchedProject> mDao = ObjectifyDao.newDao(WatchedProject.class);
    private TaskService mTaskService = new TaskService();

    public List<WatchedProject> fetchAll() {
        log.log(Level.FINEST, "Fetching all projects");

        Query<WatchedProject> q = mDao.userQuery();
        q.order("name");
        return q.list();
    }

    public WatchedProject save(WatchedProject project)
    {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        project.setOwner(loggedInUser);

        if (project.getId() != null && (project.isActiveChanged() || project.isDeletedChanged() ||
                project.isParallelChanged())) {
            mTaskService.onProjectChanged(project, mDao.key(project));
        }

        mDao.put(project);

        return project;
    }

    public void delete(WatchedProject project)
    {
        mDao.delete(project);
    }

    public Key<WatchedProject> getKey(WatchedProject project) {
        return mDao.key(project);
    }

}
