package org.dodgybits.shuffle.gwt.cursor;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public abstract class EntityCache<T> {
    private List<T> mEntities = null;
    private boolean mRequested = false;
    private List<Receiver<List<T>>> mReceivers;
    private Map<Long, T> mEntityIdMap;
    
    @Inject
    public EntityCache() {
        mReceivers = Lists.newArrayList();
    }

    public void requestEntities(Receiver<List<T>> newReceiver) {
        if (mEntities == null) {
            mReceivers.add(newReceiver);
            if (!mRequested) {
                fetchAll(new Receiver<List<T>>() {
                    @Override
                    public void onSuccess(List<T> response) {
                        mEntities = response;
                        mRequested = false;
                        for (Receiver<List<T>> receiver : mReceivers) {
                            receiver.onSuccess(response);
                        }
                        mReceivers.clear();
                        indexById();
                    }

                    @Override
                    public void onFailure(ServerFailure error) {
                        super.onFailure(error);
                        mRequested = false;
                        for (Receiver<List<T>> receiver : mReceivers) {
                            receiver.onFailure(error);
                        }
                        mReceivers.clear();
                    }
                });
                mRequested = true;
            }
        } else {
            newReceiver.onSuccess(mEntities);
        }
    }

    public T findById(Long id) {
        T entity = null;
        if (mEntityIdMap != null) {
            entity = mEntityIdMap.get(id);
        }
        return entity;
    }    

    public void clearCache() {
        mEntities = null;
        // if request in flight, leave it
    }

    abstract protected void fetchAll(Receiver<List<T>> receiver);
    
    abstract protected Long getId(T entity);

    private void indexById() {
        mEntityIdMap = Maps.uniqueIndex(mEntities, new Function<T, Long>() {
            @Override
            public Long apply(@Nullable T input) {
                return getId(input);
            }
        });
    }
    
}
