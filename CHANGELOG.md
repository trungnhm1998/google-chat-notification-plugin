# 1.2 release notes

### From this version onwards, plugin is only supported for Jenkins version 2.60.3 or higher.

New Features:

- Add support to send notifications if proxy configuration defined [#1](https://github.com/jenkinsci/google-chat-notification-plugin/issues/1)
- Suppress Info logs [#3](https://github.com/jenkinsci/google-chat-notification-plugin/issues/3).
- Removed deprecated libraries of Jenkins and replaced with newer versions. As a result of this version of plugin is only supported for Jenkins version 2.60.3 or higher.


# 1.1 release notes

New Features:

- Add support to configure entire Google Chat Room URL in credentials and use it in URL parameter as *id:configured_credential_id*.
- Default behaviour of plugin is to send notifications for all build status unless overridden with true value for defined build statuses.