package com.kenzie.appserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.controller.model.TaskCreateRequest;
import com.kenzie.appserver.controller.model.TaskUpdateRequest;
import com.kenzie.appserver.service.TaskService;
import com.kenzie.appserver.service.model.Status;
import com.kenzie.appserver.service.model.Task;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
public class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    TaskService taskService;

    private final MockNeat mockNeat = MockNeat.threadLocal();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getAllTasks_withNoTasks_noTasksReturned() throws Exception {
        mvc.perform(get("/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
    @Test
    public void getAllTasks_withTwoTasks_getsTwoTasks() throws Exception {
        String creatorUsername1 = mockNeat.names().toString();
        String title1 = mockNeat.strings().toString();
        String collaborators1 = mockNeat.actors().toString();
        String description1 = mockNeat.strings().toString();

        String creatorUsername2 = mockNeat.names().toString();
        String title2 = mockNeat.strings().toString();
        String collaborators2 = mockNeat.actors().toString();
        String description2 = mockNeat.strings().toString();

        Task task1 = new Task(creatorUsername1,
                UUID.randomUUID().toString(),
                description1,
                title1,
                collaborators1,
                "placeholder");

        Task persistedTask1 = taskService.createTask(task1);

        Task task2 = new Task(creatorUsername2,
                UUID.randomUUID().toString(),
                description2,
                title2,
                collaborators2,
                "placeholder");

        Task persistedTask2 = taskService.createTask(task2);

        mvc.perform(get("/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].creatorUsername", is(creatorUsername1)))
                .andExpect(jsonPath("$[1].creatorUsername", is(creatorUsername2)));

        taskService.deleteTask(persistedTask1.getTaskId());
        taskService.deleteTask(persistedTask2.getTaskId());
    }

    @Test
    public void createTask_CreateSuccessful() throws Exception {
    //GIVEN
        String creatorUsername = mockNeat.strings().valStr();
        String title = mockNeat.strings().valStr();
        String description = mockNeat.strings().valStr();
        String collaborators = mockNeat.strings().valStr();

        TaskCreateRequest taskCreateRequest = new TaskCreateRequest();
         taskCreateRequest.setCreatorUsername(creatorUsername);
         taskCreateRequest.setTitle(title);
         taskCreateRequest.setDescription(description);
         taskCreateRequest.setCollaborators(collaborators);

     //WHEN
       mvc.perform(post("/tasks")
               .accept(MediaType.APPLICATION_JSON)
               .contentType(MediaType.APPLICATION_JSON)
               .content(mapper.writeValueAsString(taskCreateRequest)))

       //THEN
               .andExpect(status().isCreated())
               .andExpect(jsonPath("creatorUsername")
                       .value(is(creatorUsername)))
               .andExpect(jsonPath("title")
                       .value(is(title)))
               .andExpect(jsonPath("description")
                       .value(is(description)))
               .andExpect(jsonPath("collaborators")
                       .value(is(collaborators)))
               .andExpect(jsonPath("taskId")
                       .exists())
               .andExpect(jsonPath("status")
                       .value(is(Status.TODO.toString())));

    }

    @Test
    public void createTask_missingFields_returnsBadRequest() throws Exception {
        //GIVEN
        String description = mockNeat.strings().valStr();

        TaskCreateRequest taskCreateRequest = new TaskCreateRequest();
        taskCreateRequest.setDescription(description);

        //WHEN
        mvc.perform(post("/tasks")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(taskCreateRequest)))

                //THEN
                .andExpect(status().isBadRequest());

    }

    @Test
    public void updateTask_withGoodTaskId_taskUpdated() throws Exception {
        //GIVEN
        String creatorUsername = mockNeat.names().toString();
        String title = mockNeat.departments().toString();
        String collaborators = mockNeat.names().toString();
        String description = mockNeat.strings().toString();

        Task task = new Task(creatorUsername,
                UUID.randomUUID().toString(),
                description,
                title,
                collaborators,
                "TODO");
        Task persistedTask = taskService.createTask(task);

        String newTaskTitle = "anewtitle";
        String newDescription = "arevolutionaryidea";

        TaskUpdateRequest taskUpdateRequest = new TaskUpdateRequest();
        taskUpdateRequest.setTaskId(persistedTask.getTaskId());
        taskUpdateRequest.setCollaborators(persistedTask.getCollaborators());
        taskUpdateRequest.setCreatorUsername(persistedTask.getCreatorUsername());
        taskUpdateRequest.setStatus(persistedTask.getStatus());
        taskUpdateRequest.setTitle(newTaskTitle);
        taskUpdateRequest.setDescription(newDescription);

        mvc.perform(put("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(taskUpdateRequest)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("taskId")
                        .value(is(persistedTask.getTaskId())))
                .andExpect(jsonPath("collaborators")
                        .value(is(persistedTask.getCollaborators())))
                .andExpect(jsonPath("creatorUsername")
                        .value(is(persistedTask.getCreatorUsername())))
                .andExpect(jsonPath("status")
                        .value(is(persistedTask.getStatus())))
                .andExpect(jsonPath("title")
                        .value(is(newTaskTitle)))
                .andExpect(jsonPath("description")
                        .value(is(newDescription)));

        taskService.deleteTask(persistedTask.getTaskId());
    }

    @Test
    public void updateTask_withBadTaskId_badRequest() throws Exception {
        //GIVEN
        String creatorUsername = mockNeat.names().toString();
        String title = mockNeat.departments().toString();
        String collaborators = mockNeat.names().toString();
        String description = mockNeat.strings().toString();

        Task task = new Task(creatorUsername,
                UUID.randomUUID().toString(),
                description,
                title,
                collaborators,
                "TODO");

        Task persistedTask = taskService.createTask(task);

        String newTaskTitle = "anewtitle";
        String newDescription = "arevolutionaryidea";

        TaskUpdateRequest taskUpdateRequest = new TaskUpdateRequest();
        taskUpdateRequest.setTaskId(UUID.randomUUID().toString());
        taskUpdateRequest.setCollaborators(persistedTask.getCollaborators());
        taskUpdateRequest.setCreatorUsername(persistedTask.getCreatorUsername());
        taskUpdateRequest.setStatus(persistedTask.getStatus());
        taskUpdateRequest.setTitle(newTaskTitle);
        taskUpdateRequest.setDescription(newDescription);

        mvc.perform(put("/tasks")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(taskUpdateRequest)))

                .andExpect(status().isBadRequest());

        taskService.deleteTask(persistedTask.getTaskId());
    }

    @Test
    public void getTasksByUsername_returnsAllAndOnlyTasksContainingUser() throws Exception {
        String username = "testmaster";

        String creatorUsername1 = mockNeat.names().toString();
        String title1 = mockNeat.strings().toString();
        String collaborators1 = "this,list,missing,query,param";
        String description1 = mockNeat.strings().toString();

        String creatorUsername2 = mockNeat.names().toString();
        String title2 = mockNeat.strings().toString();
        String collaborators2 = "testmaster,is,included,here";
        String description2 = mockNeat.strings().toString();

        String creatorUsername3 = mockNeat.names().toString();
        String title3 = mockNeat.strings().toString();
        String collaborators3 = "meaningless,association,of,words";
        String description3 = mockNeat.strings().toString();

        String creatorUsername4 = mockNeat.names().toString();
        String title4 = mockNeat.strings().toString();
        String collaborators4 = "testmaster,testmaster,testmaster";
        String description4 = mockNeat.strings().toString();

        String creatorUsername5 = mockNeat.names().toString();
        String title5 = mockNeat.strings().toString();
        String collaborators5 = "for,good,measure,testmaster";
        String description5 = mockNeat.strings().toString();

        Task task1 = new Task(creatorUsername1,
                UUID.randomUUID().toString(),
                description1,
                title1,
                collaborators1,
                "placeholder");

        Task persistedTask1 = taskService.createTask(task1);

        Task task2 = new Task(creatorUsername2,
                UUID.randomUUID().toString(),
                description2,
                title2,
                collaborators2,
                "placeholder");

        Task persistedTask2 = taskService.createTask(task2);

        Task task3 = new Task(creatorUsername3,
                UUID.randomUUID().toString(),
                description3,
                title3,
                collaborators3,
                "placeholder");

        Task persistedTask3 = taskService.createTask(task3);

        Task task4 = new Task(creatorUsername4,
                UUID.randomUUID().toString(),
                description4,
                title4,
                collaborators4,
                "placeholder");

        Task persistedTask4 = taskService.createTask(task4);

        Task task5 = new Task(creatorUsername5,
                UUID.randomUUID().toString(),
                description5,
                title5,
                collaborators5,
                "placeholder");

        Task persistedTask5 = taskService.createTask(task5);

        mvc.perform(get("/tasks/user/{username}", "testmaster"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

    }

    @Test
    public void deleteTaskByTaskId_taskExists_DeleteSuccessful() throws Exception {
        // GIVEN
        String creatorUsername = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        String collaborators = "me,you,him,her";
        String description = mockNeat.strings().valStr();
        String title = mockNeat.strings().valStr();
        String status = Status.IN_PROGRESS.toString();

        Task task = new Task(creatorUsername, taskId, collaborators, description, title, status);

        Task persistedTask = taskService.createTask(task);

        // WHEN
        mvc.perform(delete("/tasks/{taskId}", persistedTask.getTaskId())
                        .accept(MediaType.APPLICATION_JSON))
                // THEN
                .andExpect(status().isNoContent());
    }
    }
