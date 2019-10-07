package com.pinit.pinitmobile.tasks;

public interface AsyncTaskCallback {

    void afterExecute(int taskId, int httpStatusCode);
}
