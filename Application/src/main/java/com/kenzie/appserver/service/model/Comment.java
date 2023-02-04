package com.kenzie.appserver.service.model;

import java.util.Objects;

public class Comment {
    private String commentId;
    private String taskId;
    private String commentBody;
    private String username;


    public Comment() {
    }

    public Comment(String commentId, String taskId, String commentBody, String username) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (!Objects.equals(commentId, comment.commentId)) return false;
        if (!Objects.equals(taskId, comment.taskId)) return false;
        if (!Objects.equals(commentBody, comment.commentBody)) return false;
        return Objects.equals(username, comment.username);
    }

    @Override
    public int hashCode() {
        int result = commentId != null ? commentId.hashCode() : 0;
        result = 31 * result + (taskId != null ? taskId.hashCode() : 0);
        result = 31 * result + (commentBody != null ? commentBody.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}
