package org.dodgybits.shuffle.android.core.model.protocol;

import org.dodgybits.shuffle.android.core.model.Id;

import java.util.HashMap;
import java.util.Map;

public class HashEntityDirectory<Entity> implements MutableEntityDirectory<Entity> {

	private Map<String,Entity> mItemsByName;
	private Map<Id, Entity> mItemsById;
	
	public HashEntityDirectory() {
		mItemsByName = new HashMap<String,Entity>();
		mItemsById = new HashMap<Id,Entity>();
	}

    @Override
	public void addItem(Id id, String name, Entity item) {
		mItemsById.put(id, item);
		mItemsByName.put(name, item);
	}
	
	@Override
	public Entity findById(Id id) {
		return mItemsById.get(id);
	}
	
	@Override
	public Entity findByName(String name) {
		return mItemsByName.get(name);
	}
}
