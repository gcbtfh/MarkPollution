package com.project.markpollution.Objects;

/**
 * Created by Hung on 22-Oct-16.
 */

public class Comment {
    private String id_po;
    private String id_user;
    private String comment;
    private String time;

    public Comment() {
    }

    public Comment(String id_po, String id_user, String comment, String time) {
        this.id_po = id_po;
        this.id_user = id_user;
        this.comment = comment;
        this.time = time;
    }

    public String getId_po() {
        return id_po;
    }

    public void setId_po(String id_po) {
        this.id_po = id_po;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id_po='" + id_po + '\'' +
                ", id_user='" + id_user + '\'' +
                ", comment='" + comment + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
