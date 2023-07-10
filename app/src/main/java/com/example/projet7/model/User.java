package com.example.projet7.model;

import java.util.List;

public class User {

    String image;
    String name;
    String email;
    Boolean notification;
    List<String> favorite;

    public User(){}

    public User(String image, String name, String email, Boolean notification, List<String> favorite) {
        this.image = image;
        this.name = name;
        this.email = email;
        this.notification = notification;
        this.favorite = favorite;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNotification() {
        return notification;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    public List<String> getFavorite() {
        return favorite;
    }

    public void setFavorite(List<String> favorite) {
        this.favorite = favorite;
    }
}
