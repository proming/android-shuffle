package org.dodgybits.shuffle.android.core.util;

import java.util.Collection;

public class CollectionUtils {

    public static long[] toPrimitiveLongArray(Collection<Long> collection) {
        // Need to do this manually because we're converting to a primitive long array, not
        // a Long array.
        final int size = collection.size();
        final long[] ret = new long[size];
        // Collection doesn't have get(i).  (Iterable doesn't have size())
        int i = 0;
        for (Long value : collection) {
            ret[i++] = value;
        }
        return ret;
    }

}
