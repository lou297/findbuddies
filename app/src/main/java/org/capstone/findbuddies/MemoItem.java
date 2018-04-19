package org.capstone.findbuddies;

public class MemoItem {
    String title;
    String contents;
    String date;
    String pictureURI;

    public MemoItem(String title, String contents, String date, String pictureURI) {
        this.title = title;
        this.contents = contents;
        this.date = date;
        this.pictureURI = pictureURI;
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
}

