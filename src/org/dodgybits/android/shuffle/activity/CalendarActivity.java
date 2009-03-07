package org.dodgybits.android.shuffle.activity;

import org.dodgybits.android.shuffle.R;
import org.dodgybits.android.shuffle.activity.config.AbstractTaskListConfig;
import org.dodgybits.android.shuffle.activity.config.ListConfig;
import org.dodgybits.android.shuffle.model.Task;
import org.dodgybits.android.shuffle.provider.Shuffle;
import org.dodgybits.android.shuffle.util.MenuUtils;

import android.content.ContextWrapper;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

public class CalendarActivity extends AbstractTaskListActivity {

	private int mMode;
    private Button mDayButton;
    private Button mWeekButton;
    private Button mMonthButton;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		mMode = Shuffle.Tasks.DAY_MODE;
		mDayButton = (Button) findViewById(R.id.day_button);
		mDayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mMode = Shuffle.Tasks.DAY_MODE;
            	updateCursor();
        	}
        });
		mWeekButton = (Button) findViewById(R.id.week_button);
		mWeekButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mMode = Shuffle.Tasks.WEEK_MODE;
            	updateCursor();
        	}
        });
		mMonthButton = (Button) findViewById(R.id.month_button);
		mMonthButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mMode = Shuffle.Tasks.MONTH_MODE;
            	updateCursor();
        	}
        });
	}
	

	protected ListConfig<Task> createListConfig()
	{
		return new AbstractTaskListConfig() {

			public Uri getListContentUri() {
				return Shuffle.Tasks.cDueTasksContentURI.buildUpon().appendPath(String.valueOf(mMode)).build();
			}

		    public int getCurrentViewMenuId() {
		    	return MenuUtils.CALENDAR_ID;
		    }
		    
		    public String createTitle(ContextWrapper context)
		    {
		    	return context.getString(R.string.title_calendar, getSelectedPeriod());
		    }
		    
			@Override
			public int getContentViewResId() {
				return R.layout.calendar;
			}
			
		};
	}

	
	private void updateCursor() {
    	Cursor cursor = createItemQuery();
    	SimpleCursorAdapter adapter = (SimpleCursorAdapter)getListAdapter();
    	adapter.changeCursor(cursor);
    	setTitle(getListConfig().createTitle(this));
	}

	private String getSelectedPeriod() {
		String result = null;
		switch (mMode) {
		case Shuffle.Tasks.DAY_MODE:
			result = getString(R.string.day_button_title).toLowerCase();
			break;
		case Shuffle.Tasks.WEEK_MODE:
			result = getString(R.string.week_button_title).toLowerCase();
			break;
		case Shuffle.Tasks.MONTH_MODE:
			result = getString(R.string.month_button_title).toLowerCase();
			break;
		}
		return result;
	}
		

}
