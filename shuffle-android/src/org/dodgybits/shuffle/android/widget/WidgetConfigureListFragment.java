package org.dodgybits.shuffle.android.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.model.persistence.selector.ContextSelector;
import org.dodgybits.shuffle.android.core.model.persistence.selector.EntitySelector;
import org.dodgybits.shuffle.android.core.model.persistence.selector.ProjectSelector;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.preference.model.Preferences;
import roboguice.fragment.RoboListFragment;

public class WidgetConfigureListFragment extends RoboListFragment {
    private static final String TAG = "WidgetConfigListFrag";
    
    private static ListItem[] sListItems = new ListItem[] {
            new ListItem(R.drawable.inbox, ListQuery.inbox, TaskSelector.newBuilder().setListQuery(ListQuery.inbox).build()),
            new ListItem(R.drawable.due_actions, ListQuery.dueToday, TaskSelector.newBuilder().setListQuery(ListQuery.dueToday).build()),
            new ListItem(R.drawable.next_actions, ListQuery.nextTasks, TaskSelector.newBuilder().setListQuery(ListQuery.nextTasks).build()),
            new ListItem(R.drawable.projects, ListQuery.project, ProjectSelector.newBuilder().build()),
            new ListItem(R.drawable.applications_internet, ListQuery.context, ContextSelector.newBuilder().build()),
            new ListItem(R.drawable.ic_media_pause, ListQuery.custom, TaskSelector.newBuilder().setListQuery(ListQuery.custom).build()),
            new ListItem(R.drawable.ic_media_pause, ListQuery.tickler, TaskSelector.newBuilder().setListQuery(ListQuery.tickler).build())
    };

    private ListAdaptor mAdaptor;

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
    }

    @Override
    public void onListItemClick(ListView parent, View view, int position, long id) {
        String key = Preferences.getWidgetQueryKey(getAppWidgetId());
        ListQuery listQuery = sListItems[position].getQuery();
        Preferences.getEditor(getActivity()).putString(key, listQuery.name()).commit();

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            String message = String.format("Saving query %s under key %s", listQuery, key);
            Log.d(TAG, message);
        }

        // let widget update itself (suggested approach of calling updateAppWidget did nothing)
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {getAppWidgetId()});
        intent.setPackage(getActivity().getPackageName());
        getActivity().sendBroadcast(intent);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getAppWidgetId());
        getActivity().setResult(WidgetConfigure.RESULT_OK, resultValue);
        getActivity().finish();
    }

    private int getAppWidgetId() {
        return ((WidgetConfigure)getActivity()).getAppWidgetId();
    }

    private void setupAdaptor() {
        addNames();
        mAdaptor = new ListAdaptor(
                getActivity(), R.layout.list_item_view, sListItems);
        setListAdapter(mAdaptor);
    }

    private void addNames() {
        String[] perspectives = getResources().getStringArray(R.array.perspectives).clone();
        int viewCount = sListItems.length;
        for (int i = 0; i < viewCount; i++) {
            sListItems[i].setName(perspectives[i]);
        }
    }

    public static class ListItem {
        private final int mIconResId;
        private final ListQuery mQuery;
        private final EntitySelector mSelector;
        private String mName;

        public ListItem(int iconResId, ListQuery query, EntitySelector selector) {
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

        public EntitySelector getSelector() {
            return mSelector;
        }

        public ListQuery getQuery() {
            return mQuery;
        }
    }

    public static class ListAdaptor extends ArrayAdapter<ListItem> {

        private ListItem[] mListItems;

        public ListAdaptor(
                Context context, int textViewResourceId, ListItem[] listItems) {
            super(context, textViewResourceId, listItems);
            mListItems = listItems;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new ConfigureListItem(getContext());
            }
            ListItem listItem = getItem(position);
            ((ConfigureListItem) convertView).updateView(listItem.getName(), listItem.getIconResId());
            return convertView;
        }

    }

    public static class ConfigureListItem extends LinearLayout {
        private ImageView mIcon;
        private TextView mName;

        public ConfigureListItem(android.content.Context context) {
            super(context);
            init(context);
        }

        public void init(android.content.Context androidContext) {
            LayoutInflater vi = (LayoutInflater)androidContext.
                    getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
            vi.inflate(R.layout.widget_configure_item, this, true);

            mName = (TextView) findViewById(R.id.name);
            mIcon = (ImageView) findViewById(R.id.icon);
        }

        public void updateView(String name, int iconResId) {
            mName.setText(name);
            mIcon.setImageResource(iconResId);
        }

    }

}
