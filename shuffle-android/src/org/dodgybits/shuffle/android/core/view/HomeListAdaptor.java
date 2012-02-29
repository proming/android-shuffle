package org.dodgybits.shuffle.android.core.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import org.dodgybits.shuffle.android.core.fragment.HomeListFragment;

public class HomeListAdaptor extends ArrayAdapter<HomeListFragment.HomeItem> {

    private HomeListFragment.HomeItem[] mHomeItems;

    public HomeListAdaptor(
            Context context, int textViewResourceId, HomeListFragment.HomeItem[] homeItems) {
        super(context, textViewResourceId, homeItems);
        mHomeItems = homeItems;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new HomeListItem(getContext());
        }
        HomeListFragment.HomeItem homeItem = getItem(position);
                ((HomeListItem) convertView).updateView(homeItem.getName(), homeItem.getCount(), homeItem.getIconResId());
        return convertView;
    }
    
}
