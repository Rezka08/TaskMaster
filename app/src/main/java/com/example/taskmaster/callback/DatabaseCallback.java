package com.example.taskmaster.callback;

public interface DatabaseCallback {
    void onSuccess(T result);
    void onError(String error);
}