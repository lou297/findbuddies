package org.capstone.findbuddies;

/**
 * Created by user on 2017-12-02.
 */

public class BuddyItem {
    String name;
    String ID;
    int resId;

    public BuddyItem(String name, String ID, int resId) {
        this.name = name;
        this.ID = ID;
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    @Override
    public String toString() {
        return "BuddyItem{" +
                "name='" + name + '\'' +
                ", ID='" + ID + '\'' +
                '}';
    }
}
