package com.darksoft.ournotes.model;

import java.util.Date;

public class NoteModel {

    private String id;
    private String image;
    private Date time;

//    public NoteModel(String id, String image, String time) {
//        this.id = id;
//        this.image = image;
//        this.time = time;
//    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
