package org.dodgybits.shuffle.android.core.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class IconNameCountListAdaptor extends ArrayAdapter<IconNameCountListAdaptor.ListItem> {

    private ListItem[] mListItems;

    public IconNameCountListAdaptor(
            Context context, int textViewResourceId, ListItem[] listItems) {
        super(context, textViewResourceId, listItems);
        mListItems = listItems;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new IconNameCountListItem(getContext());
        }
        ListItem listItem = getItem(position);
                ((IconNameCountListItem) convertView).updateView(listItem.getName(), listItem.getCount(), listItem.getIconResId());
        return convertView;
    }

    public static class ListItem<Payload> {
        private final int mIconResId;
        private final Payload mPayload;
        private String mName;
        private String mCount = "";

        public ListItem(int iconResId, String name, Payload payload) {
            mIconResId = iconResId;
            mPayload = payload;
            mName = name;
        }

        public ListItem(int iconResId, Payload payload) {
            mIconResId = iconResId;
            mPayload = payload;
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

        public Payload getPayload() {
            return mPayload;
        }
    }
    
}
