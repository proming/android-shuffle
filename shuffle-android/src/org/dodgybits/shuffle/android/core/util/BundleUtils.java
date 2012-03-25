package org.dodgybits.shuffle.android.core.util;

import android.os.Bundle;
import com.google.common.collect.Lists;
import org.dodgybits.shuffle.android.core.model.Id;

import java.util.List;

public class BundleUtils {

    public static Id getId(Bundle icicle, String key) {
        Id result = Id.NONE;
        if (icicle.containsKey(key)) {
            result = Id.create(icicle.getLong(key));
        }
        return result;
    }

    public static void putId(Bundle icicle, String key, Id value) {
        if (value.isInitialised()) {
            icicle.putLong(key, value.getId());
        }
    }

    public static void putIdList(Bundle icicle, String key, List<Id> ids) {
        final int count = ids.size();
        if (count > 0) {
            long[] idArray = new long[count];
            for (int i = 0; i< count; i++) {
                idArray[i] = ids.get(i).getId();
            }
            icicle.putLongArray(key, idArray);
        }
    }

    public static List<Id> getIdList(Bundle icicle, String key) {
        List<Id> ids = Lists.newArrayList();
        if (icicle.containsKey(key)) {
            long[] idArray = icicle.getLongArray(key);
            for (long id : idArray) {
                ids.add(Id.create(id));
            }
        }
        return ids;
    }

}
