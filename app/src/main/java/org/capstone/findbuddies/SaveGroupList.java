package org.capstone.findbuddies;

import java.util.ArrayList;

/**
 * Created by user on 2018-03-08.
 */

public class SaveGroupList {
    int groupNo;
    String owner;
    String groupName;
    String password;
    ArrayList<String> members;
    ArrayList<String> membersID;

    public int getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(int groupNo) {
        this.groupNo = groupNo;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public ArrayList<String> getMembersID() {
        return membersID;
    }

    public void setMembersID(ArrayList<String> membersID) {
        this.membersID = membersID;
    }
}
