package org.capstone.findbuddies;

public class MemoItem {
    long lastSystemtime;
    int GroupNo;
    String uploader;
    String uidKey;
    int year;
    int month;
    int date;
    int hour;
    int minute;
    String title;
    String content;
    String pictureURI;
    double latitude;
    double longitude;
    String address;

    public MemoItem(long lastSystemtime, int groupNo, String uploader, String uidKey, int year, int month, int date, int hour, int minute, String title, String content, String pictureURI, double latitude, double longitude, String address) {
        this.lastSystemtime = lastSystemtime;
        GroupNo = groupNo;
        this.uploader = uploader;
        this.uidKey = uidKey;
        this.year = year;
        this.month = month;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.title = title;
        this.content = content;
        this.pictureURI = pictureURI;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public long getLastSystemtime() {
        return lastSystemtime;
    }

    public void setLastSystemtime(long lastSystemtime) {
        this.lastSystemtime = lastSystemtime;
    }

    public int getGroupNo() {
        return GroupNo;
    }

    public void setGroupNo(int groupNo) {
        GroupNo = groupNo;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getUidKey() {
        return uidKey;
    }

    public void setUidKey(String uidKey) {
        this.uidKey = uidKey;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

