package org.dodgybits.shuffle.android.list.view.task;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.dodgybits.shuffle.android.actionbarcompat.ActionBarHelper;
import org.dodgybits.shuffle.android.core.util.OSUtils;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import org.dodgybits.shuffle.android.list.model.ListTitles;

import javax.annotation.Nullable;
import java.util.List;

public class MultiTaskListFragment extends TaskListFragment {
    private static final String TAG = "MultiTaskListFragment";
    public static final String SELECTED_INDEX = "SELECTED_INDEX";

    private MultiTaskListContext mListContext;
    private SpinnerAdapter mAdapter;
    private ActionBarHelper.OnNavigationListener mListener;
    private int mOldOptions = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<ListQuery> queries = getMultiTaskListContext().getListQueries();
        List<String> names = Lists.transform(queries, new Function<ListQuery, String>() {
            @Override
            public String apply(@Nullable ListQuery input) {
                return getString(ListTitles.getTitleId(input));
            }
        });
        int spinnerResId = OSUtils.atLeastHoneycomb() ? android.R.layout.simple_spinner_dropdown_item : android.R.layout.simple_list_item_1;
        mAdapter = new ArrayAdapter(getActivity(), spinnerResId, names);
        mListener = new ActionBarHelper.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                Log.d(TAG, "Navigated to item " + itemPosition);
                mListContext.setListIndex(itemPosition);
                restartLoading();
                onVisibilityChange();
                return true;
            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Fragment doesn't have this method.  Call it manually.
            restoreInstanceState(savedInstanceState);
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onVisibilityChange() {
        Log.d(TAG, "Visibility change to " + getUserVisibleHint());
        if (getUserVisibleHint()) {
            ActionBarHelper helper = getActionBarFragmentActivity().getActionBarHelper();
            if (mOldOptions == -1) {
                mOldOptions = helper.getDisplayOptions();
                helper.setDisplayOptions(ActionBarHelper.DISPLAY_HOME_AS_UP | ActionBarHelper.DISPLAY_SHOW_HOME);
                helper.setNavigationMode(ActionBarHelper.NAVIGATION_MODE_LIST);
                helper.setListNavigationCallbacks(mAdapter, mListener);
            }
            if (helper.getSelectedNavigationIndex() != getSelectedIndex()) {
                helper.setSelectedNavigationItem(getSelectedIndex());
            }
        } else {
            if (getActivity() != null && mOldOptions != -1) {
                ActionBarHelper helper = getActionBarFragmentActivity().getActionBarHelper();
                helper.setNavigationMode(ActionBarHelper.NAVIGATION_MODE_STANDARD);
                helper.setDisplayOptions(mOldOptions);
                mOldOptions = -1;
            }
        }

        super.onVisibilityChange();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_INDEX, mListContext.getListIndex());
    }

    @Override
    void restoreInstanceState(Bundle savedInstanceState) {
        super.restoreInstanceState(savedInstanceState);
        mListContext.setListIndex(savedInstanceState.getInt(SELECTED_INDEX, 0));
    }

    private MultiTaskListContext getMultiTaskListContext() {
        initializeArgCache();
        return mListContext;
    }
    
    private int getSelectedIndex() {
        initializeArgCache();
        return mListContext.getListIndex();
    }

    private void initializeArgCache() {
        if (mListContext != null) return;
        mListContext = getArguments().getParcelable(ARG_LIST_CONTEXT);
        mListContext.setListIndex(getArguments().getInt(SELECTED_INDEX, 0));
    }

    
}
