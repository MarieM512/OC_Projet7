package com.example.projet7.model;

import java.util.HashMap;
import java.util.Map;

public class User {

    String photo;
    String name;
    String idChoice;
    String nameChoice;
    String typeChoice;
    String favorite;

    public User(){}

    public User(String photo, String name, String idChoice, String nameChoice, String typeChoice, String favorite) {
        this.photo = photo;
        this.name = name;
        this.idChoice = idChoice;
        this.nameChoice = nameChoice;
        this.typeChoice = typeChoice;
        this.favorite = favorite;
    }

    public String getIdChoice() {
        return idChoice;
    }

    public void setIdChoice(String idChoice) {
        this.idChoice = idChoice;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameChoice() {
        return nameChoice;
    }

    public void setNameChoice(String nameChoice) {
        this.nameChoice = nameChoice;
    }

    public String getTypeChoice() {
        return typeChoice;
    }

    public void setTypeChoice(String typeChoice) {
        this.typeChoice = typeChoice;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }
}
