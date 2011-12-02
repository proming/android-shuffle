package org.dodgybits.shuffle.server.service;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.web.bindery.requestfactory.shared.Request;
import com.googlecode.objectify.Query;
import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.model.Task;
import org.dodgybits.shuffle.server.model.TaskQuery;
import org.dodgybits.shuffle.server.model.TaskQueryResult;
import org.dodgybits.shuffle.shared.TaskProxy;
import org.dodgybits.shuffle.shared.TaskQueryProxy;
import org.dodgybits.shuffle.shared.TaskQueryResultProxy;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TaskDao extends ObjectifyDao<Task> {

}
