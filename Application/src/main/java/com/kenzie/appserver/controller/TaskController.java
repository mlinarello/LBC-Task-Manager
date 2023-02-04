package com.kenzie.appserver.controller;

import com.kenzie.appserver.controller.model.*;
import com.kenzie.appserver.controller.model.TaskCreateRequest;
import com.kenzie.appserver.controller.model.TaskResponse;
import com.kenzie.appserver.repositories.model.TaskRecord;
import com.kenzie.appserver.service.TaskService;
import com.kenzie.appserver.service.model.Status;
import com.kenzie.appserver.service.model.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/tasks")
public class TaskController {
    TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskCreateRequest taskCreateRequest) {
        Task task = new Task(taskCreateRequest.getCreatorUsername(),
                UUID.randomUUID().toString(),
                taskCreateRequest.getDescription(),
                taskCreateRequest.getTitle(),
                taskCreateRequest.getCollaborators(),
                Status.TODO.toString());

        try {
            taskService.createTask(task);
        }
        catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().build();
        }

        TaskResponse taskResponse = createTaskResponse(task);

        return ResponseEntity.created(URI.create("/tasks/" + taskResponse.getTaskId())).body(taskResponse);

    }

    @PutMapping
    public ResponseEntity<TaskResponse> updateTask(@RequestBody TaskUpdateRequest taskUpdateRequest){
        Task task = new Task(taskUpdateRequest.getCreatorUsername(),
                taskUpdateRequest.getTaskId(),
                taskUpdateRequest.getDescription(),
                taskUpdateRequest.getTitle(),
                taskUpdateRequest.getCollaborators(),
                taskUpdateRequest.getStatus());

        try {
            taskService.updateTask(task);
        }
        catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().build();
        }

        TaskResponse taskResponse = createTaskResponse(task);

        return ResponseEntity.ok(taskResponse);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();

        if (tasks == null || tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<TaskResponse> response = new ArrayList<>();
        for(Task task : tasks) {
            response.add(this.createTaskResponse(task));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<TaskResponse>> getTasksByUsername(@PathVariable("username") String username) {
        List<Task> tasksForUser = taskService.getTasksByUsername(username);

        if(tasksForUser == null) {
            return ResponseEntity.noContent().build();
        }

        List<TaskResponse> responseList = new ArrayList<>();
        for(Task task : tasksForUser) {
            responseList.add(createTaskResponse(task));
        }

        return ResponseEntity.ok(responseList);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity deleteTaskByTaskId(@PathVariable("taskId") String taskId) {
        taskService.deleteTask(taskId);

        return ResponseEntity.noContent().build();
    }

    private TaskResponse createTaskResponse(Task task) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTaskId(task.getTaskId());
        taskResponse.setCreatorUsername(task.getCreatorUsername());
        taskResponse.setTitle(task.getTitle());
        taskResponse.setDescription(task.getDescription());
        taskResponse.setCollaborators(task.getCollaborators());
        taskResponse.setStatus(task.getStatus());
        return taskResponse;
    }

}
