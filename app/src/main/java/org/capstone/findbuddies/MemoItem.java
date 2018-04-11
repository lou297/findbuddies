package org.capstone.findbuddies;

public class MemoItem {
    String title;
    String contents;
    String date;
    int picture;

    public MemoItem(String title, String contents, String date, int picture) {
        this.title = title;
        this.contents = contents;
        this.date = date;
        this.picture = picture;
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

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }
}

