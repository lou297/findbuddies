package org.capstone.findbuddies;

public class SaveMemo {
    String uploaderEmail;
    String lastEditDate;
    long editSystemTime;
    int checkGroupNo;
    String title;
    String memo;
    String imageUrl;
    int year;
    int month;
    int day;

    public SaveMemo() {
    }

    public String getUploaderEmail() {
        return uploaderEmail;
    }

    public void setUploaderEmail(String uploaderEmail) {
        this.uploaderEmail = uploaderEmail;
    }

    public String getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(String lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public long getEditSystemTime() {
        return editSystemTime;
    }

    public void setEditSystemTime(long editSystemTime) {
        this.editSystemTime = editSystemTime;
    }

    public int getCheckGroupNo() {
        return checkGroupNo;
    }

    public void setCheckGroupNo(int checkGroupNo) {
        this.checkGroupNo = checkGroupNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
