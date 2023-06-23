package com.example.projet7.model;

import java.util.List;

public class User {

    String photo;
    String name;
    String idChoice;
    String nameChoice;
    String typeChoice;
    String addressChoice;
    String imageChoice;
    List<String> favorite;

    public User(){}

    public User(String photo, String name, String idChoice, String nameChoice, String typeChoice, String addressChoice, String imageChoice) {
        this.photo = photo;
        this.name = name;
        this.idChoice = idChoice;
        this.nameChoice = nameChoice;
        this.typeChoice = typeChoice;
        this.addressChoice = addressChoice;
        this.imageChoice = imageChoice;
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

    public String getAddressChoice() {
        return addressChoice;
    }

    public void setAddressChoice(String addressChoice) {
        this.addressChoice = addressChoice;
    }

    public String getImageChoice() {
        return imageChoice;
    }

    public void setImageChoice(String imageChoice) {
        this.imageChoice = imageChoice;
    }

    public List<String> getFavorite() {
        return favorite;
    }

    public void setFavorite(List<String> favorite) {
        this.favorite = favorite;
    }
}
