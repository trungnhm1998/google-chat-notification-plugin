package io.cnaik.service;

import hudson.model.TaskListener;
import io.cnaik.GoogleChatNotification;

public class LogUtil {

    private GoogleChatNotification googleChatNotification;

    public LogUtil(GoogleChatNotification googleChatNotification) {
        this.googleChatNotification = googleChatNotification;
    }

    public void printLog(String message) {
        TaskListener taskListener = googleChatNotification.getTaskListener();
        if (taskListener != null) {
            taskListener.getLogger().println(message);
        }
    }

    public boolean printLogEnabled() {
        return !googleChatNotification.isSuppressInfoLoggers();
    }
}
