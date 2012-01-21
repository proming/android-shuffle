package org.dodgybits.shuffle.android.list.activity.tasklist;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import org.dodgybits.shuffle.android.core.model.persistence.selector.TaskSelector;
import org.dodgybits.shuffle.android.list.config.StandardTaskQueries;

public class TaskListContext implements Parcelable {

    private TaskSelector mSelector = StandardTaskQueries.getQuery(StandardTaskQueries.cInbox);


    /**
     * Builds an instance from the information provided in an Intent.
     * This method will perform proper validation and throw an {@link IllegalArgumentException}
     * if values in the {@link android.content.Intent} are inconsistent.
     * This will also handle the generation of default values if certain fields are unspecified
     * in the {@link android.content.Intent}.
     */
    public static TaskListContext forIntent(Context context, Intent intent) {
//        long accountId = intent.getLongExtra(EmailActivity.EXTRA_ACCOUNT_ID, Account.NO_ACCOUNT);
//        long mailboxId = intent.getLongExtra(EmailActivity.EXTRA_MAILBOX_ID, Mailbox.NO_MAILBOX);
        TaskListContext listContext = new TaskListContext(StandardTaskQueries.cInbox);
        return listContext;
    }


    public TaskListContext(String queryName) {
        mSelector = StandardTaskQueries.getQuery(queryName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public TaskSelector getSelector() {
        return mSelector;
    }
}
