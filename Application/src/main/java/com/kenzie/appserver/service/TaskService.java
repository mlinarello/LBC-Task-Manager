package com.kenzie.appserver.service;

import com.kenzie.appserver.controller.model.TaskCreateRequest;
import com.kenzie.appserver.repositories.TaskRepository;
import com.kenzie.appserver.repositories.model.TaskRecord;
import com.kenzie.appserver.service.model.Status;
import com.kenzie.appserver.service.model.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {
    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        TaskRecord taskRecord = new TaskRecord();
        taskRecord.setTaskId(task.getTaskId());
        taskRecord.setDescription(task.getDescription());
        taskRecord.setCollaborators(task.getCollaborators());
        taskRecord.setCreatorUsername(task.getCreatorUsername());
        taskRecord.setTitle(task.getTitle());
        taskRecord.setStatus(task.getStatus());

        if (task.getCreatorUsername() == null || task.getDescription() == null ||
                task.getCollaborators() == null || task.getTitle() == null)
        {
            throw new IllegalArgumentException();
        }

        taskRepository.save(taskRecord);
        return task;
    }

    public void updateTask(Task task) {
        if (taskRepository.existsById(task.getTaskId())) {
            TaskRecord taskRecord = new TaskRecord();
            taskRecord.setCreatorUsername(task.getCreatorUsername());
            taskRecord.setTaskId(task.getTaskId());
            taskRecord.setTitle(task.getTitle());
            taskRecord.setDescription(task.getDescription());
            taskRecord.setCollaborators(task.getCollaborators());
            taskRecord.setStatus(task.getStatus());
            taskRepository.save(taskRecord);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<Task>();

        Iterable<TaskRecord> taskIterator = taskRepository.findAll();
        for(TaskRecord record : taskIterator) {
            tasks.add(new Task(record.getCreatorUsername(),
                    record.getTaskId(),
                    record.getDescription(),
                    record.getTitle(),
                    record.getCollaborators(),
                    record.getStatus()));
        }

        return tasks;
    }

    public List<Task> getTasksByUsername(String username) {
        List<Task> allTasks = getAllTasks();

        List<Task> tasksForUser = new ArrayList<>();

        for(Task task : allTasks) {
            String collaboratorsCSV = task.getCollaborators();

            String[] individualCollaborators = collaboratorsCSV.split(",");

            List<String> collabsAsList = Arrays.asList(individualCollaborators);

            collabsAsList.replaceAll(String::trim);

            if(collabsAsList.contains(username)) {
                tasksForUser.add(task);
            }
        }
        return tasksForUser;
    }

    public void deleteTask(String taskId) {
        taskRepository.deleteById(taskId);
    }

}
