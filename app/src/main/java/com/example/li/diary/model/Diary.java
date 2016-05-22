package com.example.li.diary.model;

/**
 * Created by li on 2016/5/22.
 */
public class Diary {

    private String title;
    private String intro;
    private String created;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setBody(String body) {
        this.intro = body;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
