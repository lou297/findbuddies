package org.capstone.findbuddies;

public class MemoItem {
    String date_label;
    int group_label;
    String title;
    String contents;
    String date;
    String pictureURI;
    String location;

    public MemoItem(String date_label, int group_label, String title, String contents, String date, String pictureURI, String location) {
        this.date_label = date_label;
        this.group_label = group_label;
        this.title = title;
        this.contents = contents;
        this.date = date;
        this.pictureURI = pictureURI;
        this.location = location;
    }


    public String getDate_label() {
        return date_label;
    }

    public void setDate_label(String date_label) {
        this.date_label = date_label;
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

