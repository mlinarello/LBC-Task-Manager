package com.kenzie.appserver.controller.model;

public class CommentResponse {
    private String commentId;
    private String taskId;
    private String commentBody;
    private String username;

    public CommentResponse(String commentId, String taskId, String commentBody, String username) {
        this.commentId = commentId;
        this.taskId = taskId;
        this.commentBody = commentBody;
        this.username = username;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
