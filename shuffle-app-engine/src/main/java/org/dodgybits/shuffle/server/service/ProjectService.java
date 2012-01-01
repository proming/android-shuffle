package org.dodgybits.shuffle.server.service;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.Project;
import org.dodgybits.shuffle.server.model.Task;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectService {
    private static final Logger log = Logger.getLogger(ProjectService.class.getName());

    private ObjectifyDao<Project> mDao = ObjectifyDao.newDao(Project.class);
    private ObjectifyDao<Task> mTaskDao = ObjectifyDao.newDao(Task.class);

    public List<Project> fetchAll() {
        log.log(Level.FINEST, "Fetching all projects");

        Query<Project> q = mDao.userQuery();
        q.order("name");
        return q.list();
    }

    public Project save(Project project)
    {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        project.setOwner(loggedInUser);
        
        // check if active or deleted flags were changed
        if (project.getId() != null) {
            Project oldProject = mDao.get(project.getId());
            boolean activeChanged = oldProject.isActive() != project.isActive();
            boolean deletedChanged = oldProject.isDeleted() != project.isDeleted();
            if (activeChanged || deletedChanged) {
                onInheritedFlagChange(project, activeChanged, deletedChanged);
            }
        }

        mDao.put(project);
        return project;
    }

    /**
     * When a project active or deleted flag changes, all its tasks need to be updated
     * to take account of this change (since we can't use joins).
     */
    private void onInheritedFlagChange(Project project, boolean activeChanged, boolean deletedChanged) {
        Query<Task> query = mTaskDao.userQuery().filter("project", mDao.key(project)).order("order");
        List<Task> tasks = query.list();
        boolean first = true;
        for (Task task : tasks) {
        }
    }

    public void delete(Project project)
    {
        mDao.delete(project);
    }

    public Key<Project> getKey(Project project) {
        return mDao.key(project);
    }

}
