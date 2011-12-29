package org.dodgybits.shuffle.server.service;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.Project;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectService {
    private static final Logger log = Logger.getLogger(ProjectService.class.getName());

    private ProjectDao mDao = new ProjectDao();

    public List<Project> fetchAll() {
        log.log(Level.FINEST, "Fetching all projects");

        Query<Project> q = mDao.userQuery();
        return q.list();
    }

    public Project save(Project project)
    {
        AppUser loggedInUser = LoginService.getLoggedInUser();
        project.setOwner(loggedInUser);
        mDao.put(project);
        return project;
    }

    public void delete(Project project)
    {
        mDao.delete(project);
    }

    public Key<Project> getKey(Project project) {
        return mDao.key(project);
    }

}
