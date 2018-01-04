package org.capstone.findbuddies;

/**
 * Created by user on 2017-12-02.
 */

public class GroupItem {
    String name;
    String member;
    int resId;

    public GroupItem(String name,String member,int resId) {
        this.name = name;
        this.member = member;
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMember() {
        return member;
    }

    public void setMemeber(String member) {
        this.member = member;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    @Override
    public String toString() {
        return "GroupItem{" +
                "name='" + name + '\'' +
                ", member='" + member + '\'' +
                '}';
    }
}
