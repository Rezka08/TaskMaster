package com.example.taskmaster.callback;

import java.util.List;

public interface DatabaseListCallback<T> {
    void onSuccess(List<T> result);
    void onError(String error);
}