package com.project.markpollution.Objects;

/**
 * Created by Hung on 23-Oct-16.
 */

public class User {
    String id_user;
    String name_user;
    String email;
    String avatar;
    boolean is_admin;

    public User() {
    }

    public User(String id_user, String name_user, String email, String avatar, boolean is_admin) {
        this.id_user = id_user;
        this.name_user = name_user;
        this.email = email;
        this.avatar = avatar;
        this.is_admin = is_admin;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean is_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id_user='" + id_user + '\'' +
                ", name_user='" + name_user + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", is_admin=" + is_admin +
                '}';
    }
}
