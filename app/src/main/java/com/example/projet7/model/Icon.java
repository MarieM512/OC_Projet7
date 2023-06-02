package com.example.projet7.model;

import com.google.gson.annotations.SerializedName;

public class Icon {

    @SerializedName("prefix")
    private String prefix;
    @SerializedName("suffix")
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
