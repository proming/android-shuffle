package org.dodgybits.shuffle.server.servlet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.objectify.Key;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.dodgybits.shuffle.dto.ShuffleProtos;
import org.dodgybits.shuffle.dto.ShuffleProtos.Catalogue;
import org.dodgybits.shuffle.server.model.WatchedContext;
import org.dodgybits.shuffle.server.model.WatchedProject;
import org.dodgybits.shuffle.server.model.WatchedTask;
import org.dodgybits.shuffle.server.service.ContextService;
import org.dodgybits.shuffle.server.service.GlobalService;
import org.dodgybits.shuffle.server.service.ProjectService;
import org.dodgybits.shuffle.server.service.TaskService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class RestoreBackupServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(RestoreBackupServlet.class.getName());
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletFileUpload upload = new ServletFileUpload();
        upload.setFileSizeMax(100000);
        boolean deleteExisting = false;
        FileItemIterator itemIterator;
        Catalogue catalogue = null;

        try
        {
            // Parse the request
            itemIterator = upload.getItemIterator(request);

            while (itemIterator.hasNext()) {
                FileItemStream fileItemStream = itemIterator.next();
                if (fileItemStream.isFormField()) {
                    String fieldName = fileItemStream.getFieldName();
                    if ("deleteExisting".equals(fieldName))
                    {
                        String deleteStr = Streams.asString(fileItemStream.openStream());
                        deleteExisting = "on".equals(deleteStr);
                        log.log(Level.INFO, "Deleting existing {0}", deleteExisting);
                    } else {
                        log.log(Level.FINE, "Ignoring unknown form field {0}", fieldName);
                    }
                } else {
                    log.log(Level.INFO, "File {0} detected", fileItemStream.getName());
                    catalogue = Catalogue.parseFrom(fileItemStream.openStream());
                }

            }
        } catch (FileUploadException e) {
            throw new ServletException(e);
        }

        if (catalogue == null) {
            throw new ServletException("No backup file found");
        }

        if (deleteExisting) {
            GlobalService globalService = new GlobalService();
            globalService.deleteEverything();
        }

        Map<Long, Key<WatchedContext>> contextMap = saveContexts(catalogue.getContextList());
        Map<Long, Key<WatchedProject>> projectMap = saveProjects(catalogue.getProjectList(), contextMap);
        int tasksSaved = saveTasks(catalogue.getTaskList(), contextMap, projectMap);

        String responseString = String.format("Saved %s contexts %s projects and %s actions",
                contextMap.size(), projectMap.size(), tasksSaved);
        log.log(Level.INFO, responseString);

        response.getWriter().println(responseString);
        response.flushBuffer();
    }

    private Map<Long, Key<WatchedContext>> saveContexts(List<ShuffleProtos.Context> protoContexts) {
        Map<Long, Key<WatchedContext>> contextMap = Maps.newHashMap();
        ContextService contextService = new ContextService();
        List<WatchedContext> existingContexts = contextService.fetchAll();
        Map<String, WatchedContext> existingContextsByName = indexContextsByName(existingContexts);

        for (ShuffleProtos.Context protoContext : protoContexts) {
            WatchedContext backupContext = toModelContext(protoContext);
            WatchedContext existingContext = existingContextsByName.get(backupContext.getName());
            if (existingContext == null) {
                log.log(Level.FINEST, "Saving new context {0}", protoContext);
            } else {
                log.log(Level.FINEST, "Updating existing context from {0}", protoContext);
                updateExistingContext(existingContext, backupContext);
                backupContext = existingContext;
            }

            contextService.save(backupContext);
            Key<WatchedContext> key = contextService.getKey(backupContext);
            contextMap.put(protoContext.getId(), key);
        }

        return contextMap;
    }

    private void updateExistingContext(WatchedContext existingContext, WatchedContext backupContext) {
        existingContext.setColourIndex(backupContext.getColourIndex());
        existingContext.setIconName(backupContext.getIconName());
        existingContext.setActive(backupContext.isActive());
        existingContext.setDeleted(backupContext.isDeleted());
    }

    private Map<String, WatchedContext> indexContextsByName(List<WatchedContext> contexts) {
        Map<String, WatchedContext> result = Maps.newHashMapWithExpectedSize(contexts.size());
        for (WatchedContext context : contexts) {
            result.put(context.getName(), context);
        }
        return result;
    }

    private WatchedContext toModelContext(ShuffleProtos.Context protoContext) {
        WatchedContext context = new WatchedContext();
        context.setName(protoContext.getName());
        context.setColourIndex(protoContext.getColourIndex());
        if (protoContext.hasActive()) {
            context.setActive(protoContext.getActive());
        } else {
            context.setActive(true);
        }
        if (protoContext.hasDeleted()) {
            context.setDeleted(protoContext.getDeleted());
        } else {
            context.setDeleted(false);
        }
        if (protoContext.hasIcon()) {
            context.setIconName(protoContext.getIcon());
        } else {
            context.setIconName(null);
        }
        return context;
    }

    private Map<Long, Key<WatchedProject>> saveProjects(
            List<ShuffleProtos.Project> protoProjects, Map<Long, Key<WatchedContext>> contextMap) {
        Map<Long, Key<WatchedProject>> projectMap = Maps.newHashMap();
        ProjectService projectService = new ProjectService();
        List<WatchedProject> existingProjects = projectService.fetchAll();
        Map<String, WatchedProject> existingProjectsByName = indexProjectsByName(existingProjects);

        for (ShuffleProtos.Project protoProject : protoProjects) {
            WatchedProject backupProject = toModelProject(protoProject, contextMap);
            WatchedProject existingProject = existingProjectsByName.get(backupProject.getName());
            if (existingProject == null) {
                log.log(Level.FINEST, "Saving new project {0}", protoProject);
            } else {
                log.log(Level.FINEST, "Updating existing project from {0}", protoProject);
                updateExistingProject(existingProject, backupProject);
                backupProject = existingProject;
            }

            projectService.save(backupProject);
            Key<WatchedProject> key = projectService.getKey(backupProject);
            projectMap.put(protoProject.getId(), key);
        }

        return projectMap;
    }

    private void updateExistingProject(WatchedProject existingProject, WatchedProject backupProject) {
        existingProject.setParallel(backupProject.isParallel());
        existingProject.setDefaultContextKey(backupProject.getDefaultContextKey());
        existingProject.setArchived(backupProject.isArchived());
        existingProject.setActive(backupProject.isActive());
        existingProject.setDeleted(backupProject.isDeleted());
    }

    private Map<String, WatchedProject> indexProjectsByName(List<WatchedProject> projects) {
        Map<String, WatchedProject> result = Maps.newHashMapWithExpectedSize(projects.size());
        for (WatchedProject project : projects) {
            result.put(project.getName(), project);
        }
        return result;
    }

    private WatchedProject toModelProject(ShuffleProtos.Project protoProject, Map<Long, Key<WatchedContext>> contextMap) {
        WatchedProject project = new WatchedProject();
        project.setName(protoProject.getName());
        if (protoProject.hasParallel()) {
            project.setParallel(protoProject.getParallel());
        } else {
            project.setParallel(false);
        }
        project.setArchived(false);
        if (protoProject.hasDefaultContextId()) {

        }
        if (protoProject.hasDefaultContextId()) {
            Key<WatchedContext> contextKey = contextMap.get(protoProject.getDefaultContextId());
            if (contextKey != null) {
                project.setDefaultContextKey(contextKey);
            }
        }
        if (protoProject.hasActive()) {
            project.setActive(protoProject.getActive());
        } else {
            project.setActive(true);
        }
        if (protoProject.hasDeleted()) {
            project.setDeleted(protoProject.getDeleted());
        } else {
            project.setDeleted(false);
        }

        return project;
    }

    private int saveTasks(
            List<ShuffleProtos.Task> protoTasks,
            Map<Long, Key<WatchedContext>> contextMap, Map<Long, Key<WatchedProject>> projectMap) {
        List<WatchedTask> tasks = Lists.newArrayListWithCapacity(protoTasks.size());
        TaskService taskService = new TaskService();

        for (ShuffleProtos.Task protoTask : protoTasks) {
            log.log(Level.FINEST, "Saving: {0}", protoTask);
            WatchedTask task = toModelTask(protoTask, contextMap, projectMap);
            taskService.save(task);
            tasks.add(task);
        }
        
        return tasks.size();
    }
    
    private WatchedTask toModelTask(ShuffleProtos.Task protoTask,
                             Map<Long, Key<WatchedContext>> contextMap, Map<Long, Key<WatchedProject>> projectMap) {
        WatchedTask task = new WatchedTask();
        task.setDescription(protoTask.getDescription());
        task.setDetails(protoTask.getDetails());
        if (protoTask.hasActive()) {
            task.setActiveTask(protoTask.getActive());
        } else {
            task.setActiveTask(true);
        }
        if (protoTask.hasDeleted()) {
            task.setDeletedTask(protoTask.getDeleted());
        } else {
            task.setDeletedTask(false);
        }
        task.setComplete(protoTask.getComplete());
        task.setCreatedDate(toDate(protoTask.getCreated()));
        task.setOrder(protoTask.getOrder());
        task.setShowFromDate(toDate(protoTask.getStartDate()));
        task.setDueDate(toDate(protoTask.getDueDate()));
        task.setAllDay(protoTask.getAllDay());
        if (protoTask.hasProjectId()) {
            task.setProjectKey(projectMap.get(protoTask.getProjectId()));
        }
        if (protoTask.hasContextId()) {
            Key<WatchedContext> key = contextMap.get(protoTask.getContextId());
            if (key != null) {
                task.setContextKeys(Lists.newArrayList(key));
            }
        }
        return task;
    }
    
    private Date toDate(org.dodgybits.shuffle.dto.ShuffleProtos.Date protoDate) {
        Date date = null;
        if (protoDate != null && protoDate.getMillis() != 0L) {
            date = new Date(protoDate.getMillis());
        }
        return date;
    }

}
