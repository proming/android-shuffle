package org.dodgybits.shuffle.server.service;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyOpts;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.util.DAOBase;
import org.dodgybits.shuffle.server.model.*;
import org.dodgybits.shuffle.server.servlet.AuthFilter;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Generic DAO for use with Objectify
 */
public class ObjectifyDao<T> extends DAOBase
{

	static final int BAD_MODIFIERS = Modifier.FINAL | Modifier.STATIC
			| Modifier.TRANSIENT;

	static
	{
        ObjectifyService.register(WatchedTask.class);
        ObjectifyService.register(TaskQuery.class);
        ObjectifyService.register(Context.class);
        ObjectifyService.register(Project.class);
		ObjectifyService.register(AppUser.class);
	}

	protected Class<T> clazz;

    public static <T> ObjectifyDao<T> newDao(Class<T> clazz) {
        return new ObjectifyDao<T>(clazz);
    }

    public ObjectifyDao(Class<T> clazz)
	{
		this.clazz = clazz;
	}

    public ObjectifyDao(Class<T> clazz, ObjectifyOpts opts)
    {
        super(opts);
        this.clazz = clazz;
    }

    public Query<T> userQuery() {
        Query<T> q = ofy().query(clazz);
        Key<AppUser> userKey = new Key<AppUser>(AppUser.class, getCurrentUser()
                .getId());
        q.filter("owner", userKey);
        return q;
    }

    private AppUser getCurrentUser()
    {
        HttpServletRequest request = AuthFilter.getThreadLocalRequest();
        AppUser currentUser = (AppUser)request.getAttribute(LoginService.AUTH_USER);
        return currentUser;
    }

    private List<T> listByProperty(String propName, Object propValue, int offset, int limit)
    {
        Query<T> q = ofy().query(clazz);
        q.filter(propName, propValue);
        q.offset(offset);
        q.limit(limit);
        return q.list();
    }

    public Key<T> put(T entity)

    {
        return ofy().put(entity);
    }

	public Map<Key<T>, T> putAll(Iterable<T> entities)
	{
		return ofy().put(entities);
	}

	public void delete(T entity)
	{
		ofy().delete(entity);
	}

	public void deleteKey(Key<T> entityKey)
	{
		ofy().delete(entityKey);
	}

	public void deleteAll(Iterable<T> entities)
	{
		ofy().delete(entities);
	}

	public void deleteKeys(Iterable<Key<T>> keys)
	{
		ofy().delete(keys);
	}

	public T get(Long id)
	{
        return ofy().get(this.clazz, id);
	}

	public T get(Key<T> key)
	{
		return ofy().get(key);
	}

	public Map<Key<T>, T> get(Iterable<Key<T>> keys)
	{
		return ofy().get(keys);
	}

	public List<T> listAll()
	{
		Query<T> q = ofy().query(clazz);
		return q.list();
	}

	/**
	 * Convenience method to get all objects matching a single property
	 *
	 * @param propName
	 * @param propValue
	 * @return T matching Object
	 */
	public T getByProperty(String propName, Object propValue)
	{
		Query<T> q = ofy().query(clazz);
		q.filter(propName, propValue);
		Iterator<T> fetch = q.limit(2).list().iterator();
		if (!fetch.hasNext())
		{
			return null;
		}
		T obj = fetch.next();
		if (fetch.hasNext())
		{
			throw new RuntimeException(q.toString()
					+ " returned too many results");
		}
		return obj;
	}
//
//
//	public List<T> listByProperty(String propName, Object propValue)
//	{
//		Query<T> q = ofy().query(clazz);
//		q.filter(propName, propValue);
//		return q.list();
//	}
//
//	public List<Key<T>> listKeysByProperty(String propName, Object propValue)
//	{
//		Query<T> q = ofy().query(clazz);
//		q.filter(propName, propValue);
//		return q.listKeys();
//	}
//
//	public T getByExample(T exampleObj)
//	{
//		Query<T> q = buildQueryByExample(exampleObj);
//		Iterator<T> fetch = q.limit(2).list().iterator();
//		if (!fetch.hasNext())
//		{
//			return null;
//		}
//		T obj = fetch.next();
//		if (fetch.hasNext())
//		{
//			throw new RuntimeException(q.toString()
//					+ " returned too many results");
//		}
//		return obj;
//	}
//
//	public List<T> listByExample(T exampleObj)
//	{
//		Query<T> queryByExample = buildQueryByExample(exampleObj);
//		return queryByExample.list();
//	}
//
//	public Key<T> getKey(Long id)
//	{
//		return new Key<T>(this.clazz, id);
//	}
//
	public Key<T> key(T obj)
	{
		return ObjectifyService.factory().getKey(obj);
	}
//
//	public List<T> listChildren(Object parent)
//	{
//		return ofy().query(clazz).ancestor(parent).list();
//	}
//
//	public List<Key<T>> listChildKeys(Object parent)
//	{
//		return ofy().query(clazz).ancestor(parent).listKeys();
//	}
//
//	protected Query<T> buildQueryByExample(T exampleObj)
//	{
//		Query<T> q = ofy().query(clazz);
//
//		// Add all non-null properties to query filter
//		for (Field field : clazz.getDeclaredFields())
//		{
//			// Ignore transient, embedded, array, and collection properties
//			if (field.isAnnotationPresent(Transient.class)
//					|| (field.isAnnotationPresent(Embedded.class))
//					|| (field.getType().isArray())
//					|| (field.getType().isArray())
//					|| (Collection.class.isAssignableFrom(field.getType()))
//					|| ((field.getModifiers() & BAD_MODIFIERS) != 0))
//				continue;
//
//			field.setAccessible(true);
//
//			Object value;
//			try
//			{
//				value = field.get(exampleObj);
//			} catch (IllegalArgumentException e)
//			{
//				throw new RuntimeException(e);
//			} catch (IllegalAccessException e)
//			{
//				throw new RuntimeException(e);
//			}
//			if (value != null)
//			{
//				q.filter(field.getName(), value);
//			}
//		}
//
//		return q;
//	}
//


}
