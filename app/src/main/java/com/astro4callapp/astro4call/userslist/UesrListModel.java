package com.astro4callapp.astro4call.userslist;

public class UesrListModel {

    private String title;
    private String image;
    private String url;

    public UesrListModel(String title, String image, String url) {
        this.title = title;
        this.image = image;
        this.url = url;
    }

    public UesrListModel(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
