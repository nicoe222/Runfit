package com.example.nicosetiawan.runfit.Models;

import java.util.Date;

public class News {

    String title , content,image_url;

    public Date timestamp;

    public News(String title, String content, String image_url, Date timestamp) {
        this.title = title;
        this.content = content;
        this.image_url = image_url;
        this.timestamp = timestamp;
    }

    public News(){

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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
