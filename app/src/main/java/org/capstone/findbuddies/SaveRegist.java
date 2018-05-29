package org.capstone.findbuddies;

/**
 * Created by user on 2018-02-09.
 */

public class SaveRegist {
    String savedID;
    String savedName;
    String savedEmail;
    String savedPwd;
    int notificationOn;

    public String getSavedID() {
        return savedID;
    }

    public void setSavedID(String savedID) {
        this.savedID = savedID;
    }

    public String getSavedName() {
        return savedName;
    }

    public void setSavedName(String savedName) {
        this.savedName = savedName;
    }

    public String getSavedEmail() {
        return savedEmail;
    }

    public void setSavedEmail(String savedEmail) {
        this.savedEmail = savedEmail;
    }

    public String getSavedPwd() {
        return savedPwd;
    }

    public void setSavedPwd(String savedPwd) {
        this.savedPwd = savedPwd;
    }

    public int getNotificationOn() {
        return notificationOn;
    }

    public void setNotificationOn(int notificationOn) {
        this.notificationOn = notificationOn;
    }
}
