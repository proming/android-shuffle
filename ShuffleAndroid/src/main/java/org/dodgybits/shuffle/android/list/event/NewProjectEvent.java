package org.dodgybits.shuffle.android.list.event;

public class NewProjectEvent {
    
    private String mName;

    public NewProjectEvent(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
