package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.TaskRepository;
import com.kenzie.appserver.repositories.model.TaskRecord;
import com.kenzie.appserver.service.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TaskServiceTest {
    private TaskRepository taskRepository;
    private TaskService taskService;

    @BeforeEach
    public void setup() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskService(taskRepository);
    }

    @Test
    void createTask_goodTask_taskCreated() {
        String creatorUsername = "username";
        String taskId = UUID.randomUUID().toString();
        String description = "description";
        String title = "title";
        String collaborators = "you,me,them";
        String status = "TODO";

        Task task = new Task(creatorUsername, taskId, description,
                title, collaborators, status);

        TaskRecord record = new TaskRecord();
        record.setCreatorUsername(creatorUsername);
        record.setTaskId(taskId);
        record.setDescription(description);
        record.setTitle(title);
        record.setCollaborators(collaborators);
        record.setStatus(status);

        taskService.createTask(task);

        ArgumentCaptor<TaskRecord> recordCaptor = ArgumentCaptor.forClass(TaskRecord.class);
        verify(taskRepository).save(recordCaptor.capture());

        TaskRecord createdRecord = recordCaptor.getValue();

        Assertions.assertEquals(createdRecord, record);
    }

    @Test
    void createTask_taskMissingFields_throwIllegalArgumentExceptionAndNoRepoSaveCall() {
        String creatorUsername = "username";
        String taskId = UUID.randomUUID().toString();
        String description = "description";
        String title = "title";
        String collaborators = "you,me,them";
        String status = "TODO";

        Task taskMissingOneField = new Task(creatorUsername, taskId, null,
                title, collaborators, status);

        Task taskMissingTwoFields = new Task(null, taskId, description,
                title, null, status);

        Task taskWithAlmostNoFields = new Task(null, taskId, null,
                null, null, null);

        List<Task> tasks = new ArrayList<Task>();
        tasks.add(taskMissingOneField);
        tasks.add(taskMissingTwoFields);
        tasks.add(taskWithAlmostNoFields);

        for (Task task : tasks) {
            Assertions.assertThrows(IllegalArgumentException.class,
                    () -> taskService.createTask(task));
            verifyZeroInteractions(taskRepository);
        }
    }

    @Test
    void updateTask_goodTask_repoSaveCallWithGoodRecord() {
        String creatorUsername = "username";
        String taskId = UUID.randomUUID().toString();
        String description = "description";
        String title = "title";
        String collaborators = "you,me,them";
        String status = "TODO";

        Task task = new Task(creatorUsername, taskId, description,
                title, collaborators, status);

        when(taskRepository.existsById(taskId)).thenReturn(true);

        TaskRecord expectedRecord = new TaskRecord();
        expectedRecord.setCreatorUsername(creatorUsername);
        expectedRecord.setTaskId(taskId);
        expectedRecord.setDescription(description);
        expectedRecord.setTitle(title);
        expectedRecord.setCollaborators(collaborators);
        expectedRecord.setStatus(status);

        taskService.updateTask(task);

        ArgumentCaptor<TaskRecord> captor = ArgumentCaptor.forClass(TaskRecord.class);

        verify(taskRepository).save(captor.capture());

        TaskRecord savedRecord = captor.getValue();

        Assertions.assertEquals(expectedRecord, savedRecord);
    }

    @Test
    void updateTask_badTaskId_throwsException() {
        String creatorUsername = "username";
        String badTaskId = UUID.randomUUID().toString();
        String description = "description";
        String title = "title";
        String collaborators = "you,me,them";
        String status = "TODO";

        Task task = new Task(creatorUsername, badTaskId, description,
                title, collaborators, status);

        when(taskRepository.existsById(badTaskId)).thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.updateTask(task));


    }

    @Test
    void getAllTasks_getsAllTasks() {
        String creatorUsername = "username";
        String taskId = UUID.randomUUID().toString();
        String description = "description";
        String title = "title";
        String collaborators = "you,me,them";
        String status = "TODO";

        Task task = new Task(creatorUsername, taskId, description,
                title, collaborators, status);

        TaskRecord record = new TaskRecord();
        record.setCreatorUsername(creatorUsername);
        record.setTaskId(taskId);
        record.setDescription(description);
        record.setTitle(title);
        record.setCollaborators(collaborators);
        record.setStatus(status);

        String creatorUsername1 = "username1";
        String taskId1 = UUID.randomUUID().toString();
        String description1 = "description1";
        String title1 = "title1";
        String collaborators1 = "you,me,them";
        String status1 = "TODO";

        Task task1 = new Task(creatorUsername1, taskId1, description1,
                title1, collaborators1, status1);

        TaskRecord record1 = new TaskRecord();
        record1.setCreatorUsername(creatorUsername1);
        record1.setTaskId(taskId1);
        record1.setDescription(description1);
        record1.setTitle(title1);
        record1.setCollaborators(collaborators1);
        record1.setStatus(status1);

        String creatorUsername2 = "username2";
        String taskId2 = UUID.randomUUID().toString();
        String description2 = "description2";
        String title2 = "title2";
        String collaborators2 = "you,me,them";
        String status2 = "TODO";

        Task task2 = new Task(creatorUsername2, taskId2, description2,
                title2, collaborators2, status2);

        TaskRecord record2 = new TaskRecord();
        record2.setCreatorUsername(creatorUsername2);
        record2.setTaskId(taskId2);
        record2.setDescription(description2);
        record2.setTitle(title2);
        record2.setCollaborators(collaborators2);
        record2.setStatus(status2);

        List<TaskRecord> recordsList = new ArrayList<>();
        recordsList.add(record);
        recordsList.add(record1);
        recordsList.add(record2);

        when(taskRepository.findAll()).thenReturn(recordsList);

        List<Task> expected = new ArrayList<>();
        expected.add(task);
        expected.add(task1);
        expected.add(task2);

        List<Task> response = taskService.getAllTasks();

        List<String> expectedTaskIds = new ArrayList<>();
        for(Task exTask : expected) {
            expectedTaskIds.add(exTask.getTaskId());
        }

        List<String> returnedTaskIds = new ArrayList<>();
        for (Task retTask : response) {
            returnedTaskIds.add(retTask.getTaskId());
        }

        Assertions.assertEquals(returnedTaskIds, expectedTaskIds);
    }

    @Test
    void getTasksByUsername_withMixOfTasksForUserAndNot_getsOnlyTasksForUser() {
        String creatorUsername = "username";
        String taskId = UUID.randomUUID().toString();
        String description = "description";
        String title = "title";
        String collaborators = "you,me,them";
        String status = "TODO";

        Task task = new Task(creatorUsername, taskId, description,
                title, collaborators, status);

        TaskRecord record = formatTaskRecord(task);

        String taskId1 = UUID.randomUUID().toString();
        String collaborators1 = "just,includes,you";

        Task task1 = new Task(creatorUsername, taskId1, description,
                title, collaborators1, status);

        TaskRecord record1 = formatTaskRecord(task1);

        String taskId2 = UUID.randomUUID().toString();
        String collaborators2 = "no,good,key";

        Task task2 = new Task(creatorUsername, taskId2, description,
                title, collaborators2, status);

        TaskRecord record2 = formatTaskRecord(task2);

        String taskId3 = UUID.randomUUID().toString();
        String collaborators3 = "cant,find,desired,collaborator";

        Task task3 = new Task(creatorUsername, taskId3, description,
                title, collaborators3, status);

        TaskRecord record3 = formatTaskRecord(task3);


        String usernameSearch = "you";

        List<TaskRecord> repoReturnRecords = new ArrayList<>();
        repoReturnRecords.add(record);
        repoReturnRecords.add(record1);
        repoReturnRecords.add(record2);
        repoReturnRecords.add(record3);

        when(taskRepository.findAll()).thenReturn(repoReturnRecords);

        List<Task> expectedResult = new ArrayList<>();
        expectedResult.add(task);
        expectedResult.add(task1);

        List<Task> actualResult = taskService.getTasksByUsername(usernameSearch);

        Assertions.assertEquals(expectedResult.get(0).getTaskId(), actualResult.get(0).getTaskId());
        Assertions.assertEquals(expectedResult.get(1).getTaskId(), actualResult.get(1).getTaskId());
    }

    TaskRecord formatTaskRecord(Task task) {
        TaskRecord record = new TaskRecord();
        record.setTaskId(task.getTaskId());
        record.setCollaborators(task.getCollaborators());
        record.setStatus(task.getStatus());
        record.setTitle(task.getTitle());
        record.setDescription(task.getDescription());
        record.setCreatorUsername(task.getCreatorUsername());

        return record;

    }
}
