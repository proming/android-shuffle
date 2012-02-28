package org.dodgybits.shuffle.android.list.view.task;

import android.os.Parcel;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.model.ListTitles;

import javax.annotation.Nullable;
import java.util.List;

public class MultiTaskListContext extends TaskListContext {
    private static int sDefaultIndex = 0;
    private final List<ListQuery> mQueries;
    private int mListIndex;
    
    public static final Creator<MultiTaskListContext> CREATOR
            = new Creator<MultiTaskListContext>() {

        @Override
        public MultiTaskListContext createFromParcel(Parcel source) {
            List<String> queryNames =
                    Lists.newArrayList(source.createStringArray());
            List<ListQuery> queries = Lists.transform(queryNames, new Function<String, ListQuery>() {
                @Override
                public ListQuery apply(@Nullable String input) {
                    return ListQuery.valueOf(input);
                }
            });
            int index = source.readInt();
            return create(queries, index);
        }

        @Override
        public MultiTaskListContext[] newArray(int size) {
            return new MultiTaskListContext[size];
        }
    };

    
    public static final MultiTaskListContext create(List<ListQuery> queries) {
        return create(queries, sDefaultIndex);
    }

    public static final MultiTaskListContext create(List<ListQuery> queries, int index) {
        // pick the first one by default
        ListQuery query = queries.get(index);
        TaskSelector selector = TaskSelector.newBuilder().setListQuery(query).build();
        return new MultiTaskListContext(selector, ListTitles.getTitleId(query), queries, index);

    }

    private MultiTaskListContext(TaskSelector selector, int titleId, List<ListQuery> queries, int index) {
        super(selector, titleId);
        mQueries = queries;
        mListIndex = index;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        List<String> queryNames = Lists.transform(mQueries, new Function<ListQuery, String>() {
            @Override
            public String apply(@Nullable ListQuery input) {
                return input.name();
            }
        });
        dest.writeStringArray(queryNames.toArray(new String[] {}));
        dest.writeInt(mListIndex);
    }

    public int getListIndex() {
        return mListIndex;
    }

    public void setListIndex(int listIndex) {
        if (listIndex != mListIndex) {
            mListIndex = listIndex;
            ListQuery query = mQueries.get(mListIndex);
            mSelector = TaskSelector.newBuilder().setListQuery(query).build();
            mTitleId = ListTitles.getTitleId(query);
        }
    }

    public List<ListQuery> getListQueries() {
        return mQueries;
    }

    @Override
    public String toString() {
        return "[MultiTaskListContext " + mSelector.getListQuery() + "]";
    }

}
