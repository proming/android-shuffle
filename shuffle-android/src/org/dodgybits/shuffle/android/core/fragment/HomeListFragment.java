package org.dodgybits.shuffle.android.core.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.persistence.selector.ContextSelector;
import org.dodgybits.shuffle.android.core.model.persistence.selector.EntitySelector;
import org.dodgybits.shuffle.android.core.model.persistence.selector.ProjectSelector;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.core.view.HomeListAdaptor;
import org.dodgybits.shuffle.android.list.activity.EntityListsActivity;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.model.ListSettingsCache;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import roboguice.fragment.RoboListFragment;

public class HomeListFragment extends RoboListFragment {
    private static final String TAG = "HomeListFragment";
    private static final String[] PROJECTION = new String[]{"_id"};

    private static HomeItem[] sHomeItems = new HomeItem[] {
            new HomeItem(R.drawable.inbox, ListQuery.inbox, TaskSelector.newBuilder().setListQuery(ListQuery.inbox).build()),
            new HomeItem(R.drawable.due_actions, ListQuery.dueToday, TaskSelector.newBuilder().setListQuery(ListQuery.dueToday).build()),
            new HomeItem(R.drawable.next_actions, ListQuery.nextTasks, TaskSelector.newBuilder().setListQuery(ListQuery.nextTasks).build()),
            new HomeItem(R.drawable.projects, ListQuery.project, ProjectSelector.newBuilder().build()),
            new HomeItem(R.drawable.applications_internet, ListQuery.context, ContextSelector.newBuilder().build()),
            new HomeItem(R.drawable.ic_media_pause, ListQuery.custom, TaskSelector.newBuilder().setListQuery(ListQuery.custom).build()),
            new HomeItem(R.drawable.ic_media_pause, ListQuery.tickler, TaskSelector.newBuilder().setListQuery(ListQuery.tickler).build())
    };

    private AsyncTask<?, ?, ?> mTask;

    private HomeListAdaptor mAdaptor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView lv = getListView();
//        lv.setItemsCanFocus(false);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        Log.d(TAG, "-onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();

        setupAdaptor();
        mTask = new CalculateCountTask().execute();
    }

    private void setupAdaptor() {
        addNames();
        addInitialCounts();
        mAdaptor = new HomeListAdaptor(
                getActivity(), R.layout.list_item_view, sHomeItems);
        setListAdapter(mAdaptor);
    }

    private void addNames() {
        String[] perspectives = getResources().getStringArray(R.array.perspectives).clone();
        int viewCount = sHomeItems.length;
        for (int i = 0; i < viewCount; i++) {
            sHomeItems[i].setName(perspectives[i]);
        }
    }


    private void addInitialCounts() {
        int[] cachedCounts = Preferences.getTopLevelCounts(getActivity());
        int viewCount = sHomeItems.length;
        if (cachedCounts.length == viewCount) {
            for (int i = 0; i < viewCount; i++) {
                sHomeItems[i].setCount(String.valueOf(cachedCounts[i]));
            }
        }
    }


    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
        ListQuery query = sHomeItems[position].getQuery();
        Intent intent = new Intent(getActivity(), EntityListsActivity.class);
        intent.putExtra(EntityListsActivity.QUERY_NAME, query.name());
        startActivity(intent);
    }

    private class CalculateCountTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            String[] perspectives = getResources().getStringArray(R.array.perspectives);
            int length = perspectives.length;

            StringBuilder cachedCountStr = new StringBuilder();
            for (int i = 0; i < length; i++) {
                HomeItem item = sHomeItems[i];
                ListQuery query = item.getQuery();
                EntitySelector selector = item.getSelector();
                selector = selector.builderFrom().applyListPreferences(getActivity(),
                        ListSettingsCache.findSettings(query)).build();
                Cursor cursor = getActivity().getContentResolver().query(
                        selector.getContentUri(),
                        PROJECTION,
                        selector.getSelection(getActivity()),
                        selector.getSelectionArgs(),
                        selector.getSortOrder());
                int count = cursor.getCount();
                item.setCount(String.valueOf(count));
                cursor.close();
                publishProgress();

                cachedCountStr.append(count);
                if (i < length - 1) {
                    cachedCountStr.append(",");
                }
            }

            // updated cached counts
            SharedPreferences.Editor editor = Preferences.getEditor(getActivity());
            editor.putString(Preferences.TOP_LEVEL_COUNTS_KEY, cachedCountStr.toString());
            editor.commit();

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mAdaptor.notifyDataSetChanged();
        }

//        @Override
//        public void onProgressUpdate(CharSequence[]... progress) {
//            CharSequence[] labels = progress[0];
//            ArrayAdapter<CharSequence> adapter = new IconArrayAdapter(
//                    TopLevelActivity.this, R.layout.list_item_view, R.id.name, labels, mIconIds);
//            int position = getSelectedItemPosition();
//            setListAdapter(adapter);
//            setSelection(position);
//        }

        @SuppressWarnings("unused")
        public void onPostExecute() {
            mTask = null;
        }

    }

    public static class HomeItem {
        private final int mIconResId;
        private final ListQuery mQuery;
        private final EntitySelector mSelector;
        private String mName;
        private String mCount = "";

        public HomeItem(int iconResId, ListQuery query, EntitySelector selector) {
            mIconResId = iconResId;
            mSelector = selector;
            mQuery = query;
        }

        public int getIconResId() {
            return mIconResId;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getCount() {
            return mCount;
        }

        public void setCount(String count) {
            mCount = count;
        }

        public EntitySelector getSelector() {
            return mSelector;
        }

        public ListQuery getQuery() {
            return mQuery;
        }
    }
    
}
