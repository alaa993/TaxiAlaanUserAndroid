package com.taxialaan.app.Api.interfaces;


import com.taxialaan.app.Api.utils.RequestException;

public interface CallBacks<T> {
    void onSuccess(T t);

    void onFail(RequestException e);
}