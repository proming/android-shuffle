package org.dodgybits.shuffle.server.model;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Indexed;
import org.dodgybits.shuffle.server.service.ObjectifyDao;

public class UserDatastoreObject extends DatastoreObject {

    @Indexed
    private Key<AppUser> owner;

    public AppUser getOwner()
    {
        try
        {
            ObjectifyDao<AppUser> userDao = ObjectifyDao.newDao(AppUser.class);
            return userDao.get(owner);
        } catch (EntityNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setOwner(AppUser owner)
    {
        ObjectifyDao<AppUser> userDao = ObjectifyDao.newDao(AppUser.class);
        this.owner = userDao.key(owner);
    }

    public Key<AppUser> getOwnerKey()
    {
        return owner;
    }

    public void setOwnerKey(Key<AppUser> owner)
    {
        this.owner = owner;
    }

}
