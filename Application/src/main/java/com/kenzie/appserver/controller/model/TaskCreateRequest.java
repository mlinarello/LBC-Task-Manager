package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class TaskCreateRequest {

    @NotEmpty
    @JsonProperty("creatorUsername")
    private String creatorUsername;

    @NotEmpty
    @JsonProperty("title")
    private String title;
    @NotEmpty
    @JsonProperty("description")
    private String description;
    @NotEmpty
    @JsonProperty("collaborators")
    private String collaborators;

    public String getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(String collaborators) {
        this.collaborators = collaborators;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
