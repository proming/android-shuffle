package org.dodgybits.shuffle.android.list.event;

public class NewContextEvent {

    private String mName;

    public NewContextEvent(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
