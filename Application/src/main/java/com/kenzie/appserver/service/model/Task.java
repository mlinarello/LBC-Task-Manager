package com.kenzie.appserver.service.model;

public class Task {
    private final String creatorUsername;
    private final String taskId;
    private final String description;
    private final String title;
    private final String collaborators;
    private final String status;

    public Task(String creatorUsername, String taskId, String description, String title , String collaborators, String status) {
        this.creatorUsername = creatorUsername;
        this.taskId = taskId;
        this.description = description;
        this.title = title;
        this.collaborators = collaborators;
        this.status = status;
    }


    public String getCreatorUsername() {
        return creatorUsername;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getCollaborators() {
        return collaborators;
    }

    public String getStatus() {
        return this.status;
    }
}

