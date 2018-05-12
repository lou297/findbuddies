package org.capstone.findbuddies;

public class MemoItem {
    long lastSystemtime;
    String date_label;
    int year;
    int month;
    int dayOfMonth;
    int group_label;
    String title;
    String contents;
    String date;
    String pictureURI;
    String location;

    public MemoItem(long lastSystemtime,String date_label,int year,int month,int dayOfMonth, int group_label, String title, String contents, String date, String pictureURI, String location) {
        this.lastSystemtime = lastSystemtime;
        this.date_label = date_label;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.group_label = group_label;
        this.title = title;
        this.contents = contents;
        this.date = date;
        this.pictureURI = pictureURI;
        this.location = location;
    }

    public long getLastSystemtime() {
        return lastSystemtime;
    }

    public void setLastSystemtime(long lastSystemtime) {
        this.lastSystemtime = lastSystemtime;
    }

    public String getDate_label() {
        return date_label;
    }

    public void setDate_label(String date_label) {
        this.date_label = date_label;
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

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getGroup_label() {
        return group_label;
    }

    public void setGroup_label(int group_label) {
        this.group_label = group_label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPictureURI() {
        return pictureURI;
    }

    public void setPictureURI(String pictureURI) {
        this.pictureURI = pictureURI;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

