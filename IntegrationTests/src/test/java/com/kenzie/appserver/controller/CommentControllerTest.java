package com.kenzie.appserver.controller;
import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.controller.model.CommentCreateRequest;
import com.kenzie.appserver.controller.model.CommentDeleteRequest;
import com.kenzie.appserver.service.CommentService;
import com.kenzie.appserver.service.TaskService;
import com.kenzie.appserver.service.model.Comment;
import com.kenzie.appserver.service.model.Task;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@IntegrationTest
public class CommentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    CommentService commentService;

    @Autowired
    TaskService taskService;

    private final MockNeat mockNeat = MockNeat.threadLocal();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createComment_taskExists_createSuccessful() throws Exception {
        String taskId = UUID.randomUUID().toString();

        Task task = new Task("nate",
                taskId,
                "a description",
                "title",
                "these,are,collaborators",
                "TODO");

        Task persistedTask = taskService.createTask(task);

        String commentPosterUsername = "ipostcomments";
        String commentBody = "I like to leave comments";

        CommentCreateRequest commentCreateRequest = new CommentCreateRequest();
        commentCreateRequest.setTaskId(taskId);
        commentCreateRequest.setUsername(commentPosterUsername);
        commentCreateRequest.setCommentBody(commentBody);

        mvc.perform(post("/comments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreateRequest)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("commentId")
                        .exists())
                .andExpect(jsonPath("taskId")
                        .value(is(taskId)))
                .andExpect(jsonPath("commentBody")
                        .value(is(commentBody)))
                .andExpect(jsonPath("username")
                        .value(is(commentPosterUsername)));

        taskService.deleteTask(taskId);
    }

    @Test
    public void createComment_missingFields_badRequestReturned() throws Exception {
        String taskId = UUID.randomUUID().toString();
        String commentPosterUsername = "peter";
        String commentBody = "a body of a comment";

        Task task = new Task("nate",
                taskId,
                "a description",
                "title",
                "these,are,collaborators",
                "TODO");

        Task persistedTask = taskService.createTask(task);

        CommentCreateRequest commentCreateRequest = new CommentCreateRequest();
        commentCreateRequest.setTaskId(taskId);
        commentCreateRequest.setCommentBody(commentBody);

        mvc.perform(post("/comments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreateRequest)))

                .andExpect(status().isBadRequest());

        taskService.deleteTask(taskId);
    }

    @Test
    public void createComment_badTaskId_returnsBadRequest() throws Exception{
        String taskId = UUID.randomUUID().toString();
        String username = "username";
        String commentBody = "commentbody";

        CommentCreateRequest commentCreateRequest = new CommentCreateRequest();
        commentCreateRequest.setTaskId(taskId);
        commentCreateRequest.setUsername(username);
        commentCreateRequest.setCommentBody(commentBody);

        mvc.perform(post("/comments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentCreateRequest)))

                .andExpect(status().isBadRequest());
    }

    @Test
    public void getCommentsByTaskId_badTaskId_returns404NotFound() throws Exception {
        String badTaskId =  UUID.randomUUID().toString();

        mvc.perform(get("/comments/{taskId}", badTaskId))

                .andExpect(status().isNotFound());
    }
    @Test
    public void getCommentsByTaskId_commentsAndTaskExist_returnsCommentsWithOkResponse() throws Exception {
        String taskId = UUID.randomUUID().toString();

        Task task = new Task("nate",
                taskId,
                "a description",
                "title",
                "these,are,collaborators",
                "TODO");

        Task persistedTask = taskService.createTask(task);

        String commentPosterUsername1 = "peter";
        String commentBody1 = "a body of a comment";
        String commentId1 = UUID.randomUUID().toString();

        String commentPosterUsername2 = "john";
        String commentBody2 = "a collection of meaningful syntactical units";
        String commentId2 = UUID.randomUUID().toString();

        String commentPosterUsername3 = "nebuchadnezzar";
        String commentBody3 = "some things that could be said pertaining to another thing";
        String commentId3 = UUID.randomUUID().toString();

        Comment comment1 = new Comment(commentId1, taskId, commentBody1, commentPosterUsername1);
        comment1.setCommentId(commentId1);
        comment1.setTaskId(taskId);
        comment1.setCommentBody(commentBody1);
        comment1.setUsername(commentPosterUsername1);

        Comment comment2 = new Comment(commentId2, taskId, commentBody2, commentPosterUsername2);
        comment2.setCommentId(commentId2);
        comment2.setTaskId(taskId);
        comment2.setCommentBody(commentBody2);
        comment2.setUsername(commentPosterUsername2);

        Comment comment3 = new Comment(commentId3, taskId, commentBody3, commentPosterUsername3);
        comment3.setCommentId(commentId3);
        comment3.setTaskId(taskId);
        comment3.setCommentBody(commentBody3);
        comment3.setUsername(commentPosterUsername3);

        commentService.createComment(comment1);
        commentService.createComment(comment2);
        commentService.createComment(comment3);

        mvc.perform(get("/comments/{taskId}", taskId))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].taskId", is(taskId)))
                .andExpect(jsonPath("$[1].taskId", is(taskId)))
                .andExpect(jsonPath("$[2].taskId", is(taskId)))
                .andExpect(jsonPath("$[0]", not(jsonPath("$[1]"))))
                .andExpect(jsonPath("$[0]", not(jsonPath("$[2]"))))
                .andExpect(jsonPath("$[0]", not(jsonPath("$[3]"))))
                .andExpect(jsonPath("$[1]", not(jsonPath("$[2]"))))
                .andExpect(jsonPath("$[1]", not(jsonPath("$[3]"))))
                .andExpect(jsonPath("$[2]", not(jsonPath("$[3]"))));

        taskService.deleteTask(taskId);

    }

    @Test
    public void deleteComment_commentExistsValidRequest_deleteSuccessful() throws Exception {

        String taskId = UUID.randomUUID().toString();

        Task task = new Task("nate",
                taskId,
                "a description",
                "title",
                "these,are,collaborators",
                "TODO");

        Task persistedTask = taskService.createTask(task);

        String commentPosterUsername = "thegreatestname";
        String commentBody = "splendid delivery of contained potential";
        String commentId = UUID.randomUUID().toString();

        Comment comment = new Comment(commentId, taskId, commentBody, commentPosterUsername);

        commentService.createComment(comment);

        mvc.perform(delete("/comments/{commentId}", commentId))

                .andExpect(status().isOk())
                .andExpect(content().string(commentId));

        taskService.deleteTask(taskId);
    }
}
