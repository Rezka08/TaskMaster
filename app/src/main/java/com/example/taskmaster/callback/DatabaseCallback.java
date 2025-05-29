package com.example.taskmaster.callback;

import java.util.List;

public interface DatabaseCallback<T> {
    void onSuccess(T result);
    void onError(String error);
}