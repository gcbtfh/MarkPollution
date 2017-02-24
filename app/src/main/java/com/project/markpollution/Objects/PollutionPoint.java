package com.project.markpollution.Objects;

/**
 * Created by Hung on 17-Oct-16.
 */

public class PollutionPoint {
    private String id;
    private String id_cate;
    private String id_user;
    private double lat;
    private double lng;
    private String title;
    private String desc;
    private String image;
    private String time;

    public PollutionPoint() {
    }

    public PollutionPoint(String id, String id_cate, String id_user, double lat, double lng, String title, String desc, String image, String time) {
        this.id = id;
        this.id_cate = id_cate;
        this.id_user = id_user;
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_cate() {
        return id_cate;
    }

    public void setId_cate(String id_cate) {
        this.id_cate = id_cate;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return id+ "\t" +id_cate+ "\t" + id_user+ "\t" +lat+ "\t" +lng+ "\t" +title+ "\t" +desc+ "\t" +image+ "\t" +time;
    }
}
