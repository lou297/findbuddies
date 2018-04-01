package org.capstone.findbuddies;

import java.util.ArrayList;

/**
 * Created by user on 2017-12-02.
 */

public class GroupItem {
    int groupNo;
    String name;
    String member;
    ArrayList<String> memberIdList;
    int resId;

    public GroupItem(int groupNo,String name,String member,ArrayList<String> memberIdList,int resId) {
        this.groupNo = groupNo;
        this.name = name;
        this.member = member;
        this.memberIdList = memberIdList;
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

    public void setMember(String member) {
        this.member = member;
    }

    public ArrayList<String> getMemberIdList() {
        return memberIdList;
    }

    public void setMemberIdList(ArrayList<String> memberIdList) {
        this.memberIdList = memberIdList;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }

    @Override
    public String toString() {
        return "GroupItem{" +
                "name='" + name + '\'' +
                ", member='" + member + '\'' +
                '}';
    }
}
