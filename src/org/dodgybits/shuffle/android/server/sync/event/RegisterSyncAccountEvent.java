package org.dodgybits.shuffle.android.server.sync.event;

import android.accounts.Account;

public class RegisterSyncAccountEvent {

    private Account mAccount;

    public RegisterSyncAccountEvent(Account account) {
        mAccount = account;
    }

    public Account getAccount() {
        return mAccount;
    }
}
