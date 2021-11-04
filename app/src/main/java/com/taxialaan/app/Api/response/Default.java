package com.taxialaan.app.Api.response;

import com.google.gson.annotations.SerializedName;

public class Default {

    @SerializedName("message")
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
