/*
 * Copyright (C) 2009 Android Shuffle Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dodgybits.shuffle.android.core.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.core.activity.flurry.FlurryEnabledActivity;
import org.dodgybits.shuffle.android.list.model.ListQuery;
import roboguice.inject.InjectView;

import static org.dodgybits.shuffle.android.core.util.Constants.cPackage;
import static org.dodgybits.shuffle.android.core.util.Constants.cStringType;

public class HelpActivity extends FlurryEnabledActivity {
    public static final String cHelpPage = "helpPage";
    public static final String LIST_QUERY = "listQuery";
    
	@InjectView(R.id.help_screen) Spinner mHelpSpinner;
	@InjectView(R.id.help_text) TextView mHelpContent;
	@InjectView(R.id.previous_button) Button mPrevious;
	@InjectView(R.id.next_button) Button mNext;
    	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        setContentView(R.layout.help);
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        		this, R.array.help_screens,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHelpSpinner.setAdapter(adapter);
        mHelpSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
        	public void onNothingSelected(AdapterView<?> arg0) {
        		// do nothing
        	}
        	
        	public void onItemSelected(AdapterView<?> parent, View v,
        			int position, long id) {
        		int resId = HelpActivity.this.getResources().getIdentifier(
        				"help" + position, cStringType, cPackage);
        		mHelpContent.setText(HelpActivity.this.getText(resId));
        		updateNavigationButtons();
        	}
        });

        mPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		int position = mHelpSpinner.getSelectedItemPosition();
            	mHelpSpinner.setSelection(position - 1);
            }
        });        

        
        mNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		int position = mHelpSpinner.getSelectedItemPosition();
            	mHelpSpinner.setSelection(position + 1);
            }
        });        
        
        setSelectionFromBundle();
	}
	
	private void setSelectionFromBundle() {
        String queryName = getIntent().getStringExtra(LIST_QUERY);
        int position = -1;
        if (queryName != null) {
            ListQuery query = ListQuery.valueOf(queryName);
            // TODO use global list query lookup (used by help, top level and Entity list activities)
            switch (query) {
                case inbox:
                   position = 1;
                    break;
                case project:
                    position = 2;
                    break;
                case context:
                    position = 3;
                    break;
                case nextTasks:
                    position = 4;
                    break;
                case dueNextMonth:
                case dueNextWeek:
                case dueToday:
                    position = 5;
                    break;
            }
        }
        if (position == -1) {
            position = getIntent().getIntExtra(cHelpPage, 0);
        }
        mHelpSpinner.setSelection(position);
	}

	private void updateNavigationButtons() {
		int position = mHelpSpinner.getSelectedItemPosition();
		mPrevious.setEnabled(position > 0);
		mNext.setEnabled(position < mHelpSpinner.getCount() - 1);
	}
	
}
