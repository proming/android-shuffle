package org.dodgybits.shuffle.gwt.cursor;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import javax.annotation.Nullable;
import java.util.*;

public abstract class EntityCache<T extends EntityProxy> {
    private List<T> mEntities = Collections.emptyList();
    private Map<EntityProxyId<T>, T> mEntityIdMap = Collections.emptyMap();
    private ListMultimap<EntityProxyId<T>, EntityListener<T>> mEntityListeners;
    private List<EntityListListener<T>> mEntityListListeners;
    private boolean mInitialRequestSent = false;
    private boolean mRequestPending = false;

    @Inject
    public EntityCache() {
        mEntityListeners = ArrayListMultimap.create();
        mEntityListListeners = Lists.newArrayList();
    }

    public T findById(EntityProxyId<T> id) {
        return mEntityIdMap.get(id);
    }

    public void invalidateCache() {
        mEntities = Collections.emptyList();
        updateEntities();
    }

    
    public void addListener(EntityListener<T> listener, EntityProxyId<T> id) {
        mEntityListeners.put(id, listener);
        if (mInitialRequestSent) {
            if (!mRequestPending) {
                T entity = findById(id);
                listener.onEntityUpdated(entity);
            }
        } else {
            updateEntities();
            mInitialRequestSent = true;
        }
    }
    
    public void addListener(EntityListListener<T> listener) {
        mEntityListListeners.add(listener);
        if (mInitialRequestSent) {
            if (!mRequestPending) {
                listener.onEntityListUpdated(mEntities);
            }
        } else {
            updateEntities();
            mInitialRequestSent = true;
        }
    }

    public void removeListener(EntityListener<T> listener, Long id) {
        mEntityListeners.remove(id, listener);
    }

    public void removeListener(EntityListListener<T> listener) {
        mEntityListListeners.remove(listener);
    }

    public interface EntityListener<T> {
        void onEntityUpdated(T entity);
    }

    public interface EntityListListener<T> {
        void onEntityListUpdated(List<T> entities);
    }

    protected void addEntity(T entity) {
        int index = Collections.binarySearch(mEntities, entity, getComparator());
        if (index < 0) {
            mEntities.add(-index-1, entity);
        } else {
            throw new IllegalStateException("'New' entity " + entity + " already existing in list!");
        }
    }
    
    protected void updateEntity(T entity) {
        EntityProxyId<T> id = getId(entity);
        T original = findById(id);
        int index = mEntities.indexOf(original);
        mEntities.set(index, entity);
        mEntityIdMap.put(id, entity);
        notityEntityListeners(id);
    }
    
    protected void removeEntity(EntityProxyId<T> entityProxyId) {
        T entity = findById(entityProxyId);
        if (entity != null) {
            mEntityIdMap.remove(entityProxyId);
            mEntities.remove(entity);
            List<EntityListener<T>> listeners = mEntityListeners.removeAll(entityProxyId);
            for (EntityListener<T> listener : listeners) {
                listener.onEntityUpdated(null);
            }
            for (EntityListListener<T> entityListListener : mEntityListListeners) {
                entityListListener.onEntityListUpdated(mEntities);
            }
        }
    }

    abstract protected void fetchAll(Receiver<List<T>> receiver);
    
    abstract protected EntityProxyId<T> getId(T entity);

    abstract protected Comparator<T> getComparator();

    private void indexById() {
        mEntityIdMap = Maps.newHashMap(
                Maps.uniqueIndex(mEntities, new Function<T, EntityProxyId<T>>() {
            @Override
            public EntityProxyId<T> apply(@Nullable T input) {
                return getId(input);
            }
        }));
    }
    
    private void updateEntities() {
        if (!mRequestPending) {
            mRequestPending = true;
            fetchAll(new Receiver<List<T>>() {
                @Override
                public void onSuccess(List<T> entities) {
                    mEntities = entities;
                    indexById();
                    notifyListListeners();
                    notityEntityListeners();
                    mRequestPending = false;
                }

                @Override
                public void onFailure(ServerFailure error) {
                    super.onFailure(error);
                    mRequestPending = false;
                }
            });
        }
    }

    private void notityEntityListeners(EntityProxyId<T> id) {
        List<EntityListener<T>> listeners = mEntityListeners.get(id);
        T entity = findById(id);
        for (EntityListener<T> listener : listeners) {
            listener.onEntityUpdated(entity);
        }
    }

    private void notityEntityListeners() {
        Set<EntityProxyId<T>> ids = mEntityListeners.keySet();
        for (EntityProxyId<T> id : ids) {
            List<EntityListener<T>> listeners = mEntityListeners.get(id);
            T entity = findById(id);
            for (EntityListener<T> listener : listeners) {
                listener.onEntityUpdated(entity);
            }
        }
    }

    private void notifyListListeners() {
        for (EntityListListener<T> listener : mEntityListListeners) {
            listener.onEntityListUpdated(mEntities);
        }
    }

}
