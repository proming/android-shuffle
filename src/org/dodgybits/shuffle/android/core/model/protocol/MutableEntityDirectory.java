package org.dodgybits.shuffle.android.core.model.protocol;

import org.dodgybits.shuffle.android.core.model.Id;

public interface MutableEntityDirectory<Entity> extends EntityDirectory<Entity> {

    void addItem(Id id, String name, Entity item);

}
