package org.dodgybits.shuffle.server.model;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Indexed;
import org.dodgybits.shuffle.server.service.AppUserDao;

public class UserDatastoreObject extends DatastoreObject {

    @Indexed
    private Key<AppUser> owner;

    public AppUser getOwner()
    {
        try
        {
            return new AppUserDao().get(owner);
        } catch (EntityNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setOwner(AppUser owner)
    {
        this.owner = new AppUserDao().key(owner);
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
