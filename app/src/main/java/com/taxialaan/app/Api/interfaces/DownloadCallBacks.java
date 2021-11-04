package com.taxialaan.app.Api.interfaces;


public interface DownloadCallBacks<T> {
    void onSuccess(T t);

    void onProgress(int percent);

    void onFail(Exception e);
}