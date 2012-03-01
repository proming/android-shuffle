package org.dodgybits.shuffle.android.list.event;

public class QuickAddEvent {
    private String mValue;

    public QuickAddEvent(String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }
}
