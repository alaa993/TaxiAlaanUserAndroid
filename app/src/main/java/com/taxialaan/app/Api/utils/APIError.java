package com.taxialaan.app.Api.utils;

import com.google.gson.annotations.SerializedName;

public class APIError {

    private int statusCode;

    @SerializedName("Message")
    private String message;

    public APIError() {
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}