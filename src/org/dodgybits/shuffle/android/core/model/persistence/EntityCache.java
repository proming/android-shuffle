package org.dodgybits.shuffle.android.core.model.persistence;

import org.dodgybits.shuffle.android.core.model.Entity;
import org.dodgybits.shuffle.android.core.model.Id;

import java.util.List;

public interface EntityCache<E extends Entity> {

	E findById(Id localId);

    /**
     * Any ids that don't match are ignored, so the returned entity list
     * may be shorter than the id list.
     *
     * @param localIds
     * @return a list of entities that match the given list of ids
     */
    List<E> findById(List<Id> localIds);
    void flush();
}