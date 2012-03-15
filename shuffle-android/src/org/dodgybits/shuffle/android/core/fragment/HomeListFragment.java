package org.dodgybits.shuffle.android.core.fragment;

import android.app.Activity;
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
import org.dodgybits.shuffle.android.core.view.IconNameCountListAdaptor;
import org.dodgybits.shuffle.android.core.view.IconNameCountListAdaptor.ListItem;
import org.dodgybits.shuffle.android.core.view.ListIcons;
import org.dodgybits.shuffle.android.list.activity.EntityListsActivity;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.model.ListSettingsCache;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import roboguice.fragment.RoboListFragment;

public class HomeListFragment extends RoboListFragment {
    private static final String TAG = "HomeListFragment";
    private static final String[] PROJECTION = new String[]{"_id"};

    private static ListItem<HomeEntry>[] sListItems = null;
    
    private void createListItems() {
        if (sListItems == null) {
            String[] perspectives = getResources().getStringArray(R.array.perspectives).clone();
            int[] cachedCounts = Preferences.getTopLevelCounts(getActivity());
            int i = 0;
            sListItems = new ListItem[] {
                    createTaskListItem(ListIcons.INBOX, perspectives[i], getInitialCount(cachedCounts, i++), ListQuery.inbox),
                    createTaskListItem(ListIcons.DUE_TODAY, perspectives[i], getInitialCount(cachedCounts, i++), ListQuery.dueToday),
                    createTaskListItem(ListIcons.NEXT_TASKS, perspectives[i], getInitialCount(cachedCounts, i++), ListQuery.nextTasks),
                    createListItem(ListIcons.PROJECTS, perspectives[i], getInitialCount(cachedCounts, i++), ListQuery.project, ProjectSelector.newBuilder().build()),
                    createListItem(ListIcons.CONTEXTS, perspectives[i], getInitialCount(cachedCounts, i++), ListQuery.context, ContextSelector.newBuilder().build()),
                    createTaskListItem(ListIcons.CUSTOM, perspectives[i], getInitialCount(cachedCounts, i++), ListQuery.custom),
                    createTaskListItem(ListIcons.TICKLER, perspectives[i], getInitialCount(cachedCounts, i++), ListQuery.tickler)
            };
        };
    }
    
    private String getInitialCount(int[] cachedCounts, int index) {
        String result = "";
        if (cachedCounts != null && cachedCounts.length > index) {
            result = String.valueOf(cachedCounts[index]);
        }
        return result;
    }

    private ListItem<HomeEntry> createTaskListItem(int iconResId, String name, 
                                                   String initialCount, ListQuery query) {
        final TaskSelector selector = TaskSelector.newBuilder().setListQuery(query).build();
        return createListItem(iconResId, name, initialCount, query, selector);
    }

    private ListItem<HomeEntry> createListItem(int iconResId, String name, String initialCount, 
                                               ListQuery query, EntitySelector selector) {
        HomeEntry entry = new HomeEntry(query, selector);
        ListItem<HomeEntry> listItem = new ListItem<HomeEntry>(iconResId, name, entry);
        listItem.setCount(initialCount);
        return listItem;
    }
    

    private AsyncTask<?, ?, ?> mTask;

    private IconNameCountListAdaptor mAdaptor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        createListItems();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView lv = getListView();
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
        mAdaptor = new IconNameCountListAdaptor(
                getActivity(), R.layout.list_item_view, sListItems);
        setListAdapter(mAdaptor);
    }


    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
        Intent intent = sListItems[position].getPayload().createIntent(getActivity());
        startActivity(intent);
    }

    private class CalculateCountTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            int length = sListItems.length;
            StringBuilder cachedCountStr = new StringBuilder();
            for (int i = 0; i < length; i++) {
                ListItem<HomeEntry> item = sListItems[i];
                String count = item.getPayload().getCount(getActivity());
                item.setCount(count);
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

        @Override
        protected void onPostExecute(Void aVoid) {
            mTask = null;
        }

    }
    
    private static class HomeEntry {
        final ListQuery mListQuery;
        final EntitySelector mSelector;

        private HomeEntry(ListQuery listQuery, EntitySelector selector) {
            mListQuery = listQuery;
            mSelector = selector;
        }

        public String getCount(Activity activity) {
            EntitySelector selector = mSelector.builderFrom().applyListPreferences(activity,
                    ListSettingsCache.findSettings(mListQuery)).build();
            Cursor cursor = activity.getContentResolver().query(
                    selector.getContentUri(),
                    PROJECTION,
                    selector.getSelection(activity),
                    selector.getSelectionArgs(),
                    selector.getSortOrder());
            int count = cursor.getCount();
            cursor.close();
            return String.valueOf(count);
        }

        public Intent createIntent(Activity activity) {
            Intent intent = new Intent(activity, EntityListsActivity.class);
            intent.putExtra(EntityListsActivity.QUERY_NAME, mListQuery.name());
            return intent;
        }
    };


    
}
