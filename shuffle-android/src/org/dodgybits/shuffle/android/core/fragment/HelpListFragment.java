package org.dodgybits.shuffle.android.core.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.dodgybits.android.shuffle.R;
import roboguice.fragment.RoboFragment;

public class HelpListFragment extends RoboFragment {
    private static final String TAG = "HelpListFragment";

    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    private TextView mHelpText;
    private CharSequence mContent;
    private String mTitle;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.help, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updateText();
    }

    @Override
    public void onResume() {
        super.onResume();

        onVisibilityChange();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        onVisibilityChange();
    }

    private void onVisibilityChange() {
        if (getUserVisibleHint()) {
            updateTitle();
        }
    }
    private void updateText() {
        mHelpText = (TextView) getView().findViewById(R.id.help_text);
        mHelpText.setText(getContent());
    }

    private void updateTitle() {
        getActivity().setTitle(getString(R.string.title_help) + " > " + getTitle());
    }

    private void initializeArgCache() {
        if (mTitle != null) return;
        Bundle args = getArguments();
        mTitle = args.getString(TITLE);
        mContent = args.getCharSequence(CONTENT);
    }

    private String getTitle() {
        initializeArgCache();
        return mTitle;
    }

    private CharSequence getContent() {
        initializeArgCache();
        return mContent;
    }

}
