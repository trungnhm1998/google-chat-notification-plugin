package io.cnaik.service;

import hudson.FilePath;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.cnaik.GoogleChatNotification;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.tokenmacro.TokenMacro;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;

public class CommonUtil {

    private GoogleChatNotification googleChatNotification;
    private TaskListener taskListener;
    private FilePath ws;

    public CommonUtil(GoogleChatNotification googleChatNotification,
                      TaskListener taskListener,
                      FilePath ws) {
        this.googleChatNotification = googleChatNotification;
        this.taskListener = taskListener;
        this.ws = ws;
    }

    public void sendNotification(String json) {

        String[] urlDetails = googleChatNotification.getUrl().split(",");
        Response response = null;
        String[] url;

        for(String urlDetail: urlDetails) {

            response = call(urlDetail, json);

            if (response == null
                    && StringUtils.isNotEmpty(urlDetail)
                    && urlDetail.trim().startsWith("id:")) {

                url = urlDetail.trim().split("id:");

                CredentialUtil credentialUtil = new CredentialUtil();
                StringCredentials stringCredentials = credentialUtil.lookupCredentials(url[1]);

                if (stringCredentials != null
                        && stringCredentials.getSecret() != null) {

                    response = call(stringCredentials.getSecret().getPlainText(), json);
                }
            }

            if (taskListener != null) {
                if(response == null) {
                    taskListener.getLogger().println("Invalid Google Chat Notification URL found: " + urlDetail);
                } else {
                    taskListener.getLogger().println("Chat Notification Response: " + response.print());
                }
            }
        }
    }

    public String formResultJSON(Run build) {

        String defaultMessage = escapeSpecialCharacter(replaceJenkinsKeywords(googleChatNotification.getMessage(), build));
        return "{ 'text': '" + defaultMessage + "'}";
    }

    public String replaceJenkinsKeywords(String inputString, Run build) {

        if(StringUtils.isEmpty(inputString)) {
            return inputString;
        }

        try {

            if(taskListener != null) {
                taskListener.getLogger().println("ws: " + ws + " , build: " + build);
            }

            return TokenMacro.expandAll(build, ws, taskListener, inputString, false, null);
        } catch (Exception e) {
            if(taskListener != null) {
                taskListener.getLogger().println("Exception in Token Macro expansion: " + e);
            }
        }
        return inputString;
    }

    public boolean checkWhetherToSend(Run build) {

        boolean result = false;

        if(build == null || build.getResult() == null || googleChatNotification == null) {
            return result;
        }

        Run prevRun = build.getPreviousBuild();
        Result previousResult = (prevRun != null) ? prevRun.getResult() : Result.SUCCESS;

        if(googleChatNotification.isNotifyAborted()
                && Result.ABORTED == build.getResult()) {

            result = true;

        } else if(googleChatNotification.isNotifyFailure()
                && Result.FAILURE == build.getResult()) {

            result = true;

        } else if(googleChatNotification.isNotifyNotBuilt()
                && Result.NOT_BUILT == build.getResult()) {

            result = true;

        } else if(googleChatNotification.isNotifySuccess()
                && Result.SUCCESS == build.getResult()) {

            result = true;

        } else if(googleChatNotification.isNotifyUnstable()
                && Result.UNSTABLE == build.getResult()) {

            result = true;

        } else if(googleChatNotification.isNotifyBackToNormal() && Result.SUCCESS == build.getResult()
                    && (   Result.ABORTED == previousResult
                        || Result.FAILURE == previousResult
                        || Result.UNSTABLE == previousResult
                        || Result.NOT_BUILT == previousResult) ) {

            result = true;

        }

        return result;
    }

    public boolean checkPipelineFlag(Run build) {
        if(googleChatNotification != null &&
                !googleChatNotification.isNotifyAborted() &&
                !googleChatNotification.isNotifyBackToNormal() &&
                !googleChatNotification.isNotifyFailure() &&
                !googleChatNotification.isNotifyNotBuilt() &&
                !googleChatNotification.isNotifySuccess() &&
                !googleChatNotification.isNotifyUnstable()) {
            return true;
        }
        return checkWhetherToSend(build);
    }

    public String escapeSpecialCharacter(String input) {

        String output = input;

        if(StringUtils.isNotEmpty(output)) {
            output = output.replace("{", "\\{");
            output = output.replace("}", "\\}");
            output = output.replace("'", "\\'");
        }

        return output;
    }

    private boolean checkIfValidURL(String url) {
        return (StringUtils.isNotEmpty(url)
                && url.trim().contains("https")
                && url.trim().contains("?"));
    }

    private String[] splitURLOnQuestionMark(String url) {
        return url.trim().split("\\?");
    }

    private Response call(String urlDetail, String json) {

        if (checkIfValidURL(urlDetail)) {

            String[] url = splitURLOnQuestionMark(urlDetail);

            return given(with().baseUri(url[0]))
                    .contentType(ContentType.JSON).queryParam(url[1])
                    .urlEncodingEnabled(false).body(json).log()
                    .all().post();
        }
        return null;
    }
}
