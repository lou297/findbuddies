package org.capstone.findbuddies;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 2018-03-19.
 */

public class SaveUserGPS {
    String userEmail;
    boolean gpsPermission;
    ArrayList<LocationInfo> gpsList;
    ArrayList<Date> timeList;

    public SaveUserGPS() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isGpsPermission() {
        return gpsPermission;
    }

    public void setGpsPermission(boolean gpsPermission) {
        this.gpsPermission = gpsPermission;
    }

    public ArrayList<LocationInfo> getGpsList() {
        return gpsList;
    }

    public void setGpsList(ArrayList<LocationInfo> gpsList) {
        this.gpsList = gpsList;
    }

    public ArrayList<Date> getTimeList() {
        return timeList;
    }

    public void setTimeList(ArrayList<Date> timeList) {
        this.timeList = timeList;
    }
}
