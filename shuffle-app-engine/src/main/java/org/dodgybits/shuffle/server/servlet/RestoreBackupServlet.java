package org.dodgybits.shuffle.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.dodgybits.shuffle.dto.ShuffleProtos;
import org.dodgybits.shuffle.dto.ShuffleProtos.Catalogue;
import org.dodgybits.shuffle.server.model.Task;
import org.dodgybits.shuffle.server.service.TaskDao;
import org.dodgybits.shuffle.server.service.TaskService;

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
                    int tasksSaved = saveAll(catalogue);
                    response.getWriter().println("Saved " + tasksSaved + " actions.");
                    response.flushBuffer();
                }
            }
        } catch (FileUploadException e) {
            throw new ServletException(e);
        }
    }
    
    private int saveAll(Catalogue catalogue) {
        List<ShuffleProtos.Task> protoTasks = catalogue.getTaskList();
        List<Task> tasks = new ArrayList<Task>(protoTasks.size());
        
        TaskService service = new TaskService();
        
        for (ShuffleProtos.Task protoTask : protoTasks) {
            logger.info("Saving task: " + protoTask.toString());
            Task task = toModelTask(protoTask);
            service.save(task);
            tasks.add(task);
        }
        
        return tasks.size();
    }
    
    private Task toModelTask(ShuffleProtos.Task protoTask) {
        Task task = new Task();
        task.setDescription(protoTask.getDescription());
        task.setDetails(protoTask.getDetails());
        task.setActive(protoTask.getActive());
        task.setComplete(protoTask.getComplete());
        task.setCreatedDate(new Date(protoTask.getCreated().getMillis()));
        task.setDeleted(protoTask.getDeleted());
        task.setModifiedDate(new Date(protoTask.getModified().getMillis()));
        task.setOrder(protoTask.getOrder());
        return task;
    }

}
