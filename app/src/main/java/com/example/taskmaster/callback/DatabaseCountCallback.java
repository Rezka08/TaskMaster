package com.example.taskmaster.callback;

public interface DatabaseCountCallback {
    void onSuccess(Integer count);
    void onError(String error);
}