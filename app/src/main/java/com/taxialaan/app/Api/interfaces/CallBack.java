package com.taxialaan.app.Api.interfaces;


import com.taxialaan.app.Api.utils.RequestException;

public abstract class CallBack<T> implements CallBacks<T> {
    @Override
    public void onSuccess(T t) {

    }

    @Override
    public void onFail(RequestException e) {

    }
}
