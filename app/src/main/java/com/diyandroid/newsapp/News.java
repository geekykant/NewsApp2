package com.diyandroid.newsapp;

public class News {

    private String sectionName, webTitle, webUrl, webPublicationDate;

    public News(String sectionName, String webTitle, String webUrl, String webPublicationDate) {
        this.sectionName = sectionName;
        this.webTitle = webTitle;
        this.webUrl = webUrl;
        this.webPublicationDate = webPublicationDate;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

}
