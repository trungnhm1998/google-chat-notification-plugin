package io.cnaik.service;

import hudson.model.TaskListener;

public class LogUtil {

    private TaskListener taskListener;

    public LogUtil(TaskListener taskListener) {
        this.taskListener = taskListener;
    }

    public void printLog(String message) {
        if (taskListener != null) {
            taskListener.getLogger().println(message);
        }
    }
}
