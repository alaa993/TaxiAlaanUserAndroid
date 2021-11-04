package com.taxialaan.app.Api.utils;

public class RequestException extends Exception {

    public RequestException(String message, int responseCode) {
        super(message);
        this.responseCode = responseCode;
    }

    private int responseCode;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
