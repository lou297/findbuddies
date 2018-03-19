package org.capstone.findbuddies;

import java.util.ArrayList;

/**
 * Created by user on 2018-03-19.
 */

public class SaveUserGPS {
    String userName;
    ArrayList<Integer> groupList;
    ArrayList<Integer> gpsList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<Integer> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<Integer> groupList) {
        this.groupList = groupList;
    }

    public ArrayList<Integer> getGpsList() {
        return gpsList;
    }

    public void setGpsList(ArrayList<Integer> gpsList) {
        this.gpsList = gpsList;
    }
}
