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
import org.dodgybits.shuffle.server.model.Context;
import org.dodgybits.shuffle.server.model.Project;
import org.dodgybits.shuffle.server.model.Task;
import org.dodgybits.shuffle.server.service.ContextService;
import org.dodgybits.shuffle.server.service.ProjectService;
import org.dodgybits.shuffle.server.service.TaskService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class RestoreBackupServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RestoreBackupServlet.class.getName());
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
     // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        logger.log(Level.FINE, "Request multipart " + isMultipart);
        
     // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload();

        // Parse the request
        try
        {
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                if (item.isFormField()) {
                    System.out.println("Form field " + name + " with value "
                        + Streams.asString(stream) + " detected.");
                } else {
                    System.out.println("File field " + name + " with file name "
                        + item.getName() + " detected.");
                    // Process the input stream
                    Catalogue catalogue = Catalogue.parseFrom(stream);

                    Map<Long, Key<Context>> contextMap = saveContexts(catalogue);
                    Map<Long, Key<Project>> projectMap = saveProjects(catalogue, contextMap);
                    int tasksSaved = saveTasks(catalogue, contextMap, projectMap);
                    response.getWriter().println("Saved " + tasksSaved + " actions.");
                    response.flushBuffer();
                }
            }
        } catch (FileUploadException e) {
            throw new ServletException(e);
        }
    }

    private Map<Long, Key<Context>> saveContexts(Catalogue catalogue) {
        List<ShuffleProtos.Context> protoContexts = catalogue.getContextList();
        Map<Long, Key<Context>> contextMap = Maps.newHashMap();
        ContextService contextService = new ContextService();

        for (ShuffleProtos.Context protoContext : protoContexts) {
            logger.info("Saving: " + protoContext.toString());
            Context context = toModelContext(protoContext);
            contextService.save(context);
            Key<Context> key = contextService.getKey(context);
            contextMap.put(protoContext.getId(), key);
        }

        return contextMap;
    }

    private Context toModelContext(ShuffleProtos.Context protoContext) {
        Context context = new Context();
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

    private Map<Long, Key<Project>> saveProjects(Catalogue catalogue, Map<Long, Key<Context>> contextMap) {
        List<ShuffleProtos.Project> protoProjects = catalogue.getProjectList();
        Map<Long, Key<Project>> projectMap = Maps.newHashMap();
        ProjectService projectService = new ProjectService();

        for (ShuffleProtos.Project protoProject : protoProjects) {
            logger.info("Saving: " + protoProject.toString());
            Project project = toModelProject(protoProject, contextMap);
            projectService.save(project);
            Key<Project> key = projectService.getKey(project);
            projectMap.put(protoProject.getId(), key);
        }

        return projectMap;
    }

    private Project toModelProject(ShuffleProtos.Project protoProject, Map<Long, Key<Context>> contextMap) {
        Project project = new Project();
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
            Key<Context> contextKey = contextMap.get(protoProject.getDefaultContextId());
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

    private int saveTasks(Catalogue catalogue, Map<Long, Key<Context>> contextMap, Map<Long, Key<Project>> projectMap) {
        List<ShuffleProtos.Task> protoTasks = catalogue.getTaskList();
        List<Task> tasks = Lists.newArrayListWithCapacity(protoTasks.size());
        TaskService taskService = new TaskService();

        for (ShuffleProtos.Task protoTask : protoTasks) {
            logger.info("Saving: " + protoTask.toString());
            Task task = toModelTask(protoTask, contextMap, projectMap);
            taskService.save(task);
            tasks.add(task);
        }
        
        return tasks.size();
    }
    
    private Task toModelTask(ShuffleProtos.Task protoTask,
                             Map<Long, Key<Context>> contextMap, Map<Long, Key<Project>> projectMap) {
        Task task = new Task();
        task.setDescription(protoTask.getDescription());
        task.setDetails(protoTask.getDetails());
        if (protoTask.hasActive()) {
            task.setActive(protoTask.getActive());
        } else {
            task.setActive(true);
        }
        if (protoTask.hasDeleted()) {
            task.setDeleted(protoTask.getDeleted());
        } else {
            task.setDeleted(false);
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
            Key<Context> key = contextMap.get(protoTask.getContextId());
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
