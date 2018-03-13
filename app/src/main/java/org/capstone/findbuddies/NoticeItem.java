package org.capstone.findbuddies;

/**
 * Created by user on 2018-03-13.
 */

public class NoticeItem {
    String Owner;
    String GroupName;
    String MemberList;

    public NoticeItem(String owner, String groupName, String memberList) {
        Owner = owner;
        GroupName = groupName;
        MemberList = memberList;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getMemberList() {
        return MemberList;
    }

    public void setMemberList(String memberList) {
        MemberList = memberList;
    }
}
