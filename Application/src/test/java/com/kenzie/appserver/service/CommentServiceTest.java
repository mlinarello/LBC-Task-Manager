package com.kenzie.appserver.service;

import com.kenzie.appserver.controller.model.CommentResponse;
import com.kenzie.appserver.repositories.CommentRepository;
import com.kenzie.appserver.repositories.TaskRepository;
import com.kenzie.appserver.repositories.model.CommentRecord;
import com.kenzie.appserver.service.model.Comment;
import org.checkerframework.checker.units.qual.C;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.validation.constraints.AssertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CommentServiceTest {

    private CommentRepository commentRepository;
    private TaskRepository taskRepository;
    private CommentService commentService;

    @BeforeEach
    void setup() {
        commentRepository = mock(CommentRepository.class);
        taskRepository = mock(TaskRepository.class);
        commentService = new CommentService(commentRepository, taskRepository);
    }

    @Test
    void createComment_buildsRecordProperlyAndCallsRepoSave() {
        String commentId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        String commentBody = "This is a great comment.";
        String username = "uniqueusername";

        Comment comment = new Comment(commentId, taskId, commentBody, username);

        ArgumentCaptor<CommentRecord> commentRecordCaptor = ArgumentCaptor.forClass(CommentRecord.class);

        Comment returnedComment = commentService.createComment(comment);

        Assertions.assertNotNull(returnedComment);

        verify(commentRepository).save(commentRecordCaptor.capture());

        CommentRecord commentRecord = commentRecordCaptor.getValue();

        Assertions.assertEquals(comment, returnedComment);
        Assertions.assertEquals(commentRecord.getCommentId(), commentId);
        Assertions.assertEquals(commentRecord.getTaskId(), taskId);
        Assertions.assertEquals(commentRecord.getCommentBody(), commentBody);
        Assertions.assertEquals(commentRecord.getUsername(), username);

    }
    @Test
    void createComment_missingFields_throwsIllegalArgumentExceptionBeforeRepoSave() {
        String commentId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        String commentBody = "the body of a comment";
        String username = "usernameiguess";

        Comment commentMissingCommentId = new Comment(null, taskId, commentBody, username);
        Comment commentMissingCommentBody = new Comment(commentId, taskId, null, username);
        Comment commentMissingUsername = new Comment(commentId, taskId, commentBody, null);

        List<Comment> testComments = new ArrayList<Comment>();
        testComments.add(commentMissingCommentId);
        testComments.add(commentMissingCommentBody);
        testComments.add(commentMissingUsername);

        for(Comment testComment : testComments) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> commentService.createComment(testComment));
        }
    }

    @Test
    void getCommentsByTaskId_withTasksForGivenTaskIdAndCommentsForOtherTasks_returnsOnlyCommentsForGivenTaskId() {
        String goodTaskId = UUID.randomUUID().toString();
        String badTaskId = UUID.randomUUID().toString();

        Comment goodComment1 = new Comment(UUID.randomUUID().toString(),
                goodTaskId, "body1", "user1");

        Comment goodComment2 = new Comment(UUID.randomUUID().toString(),
                goodTaskId, "body2", "user2");

        Comment badComment1 = new Comment(UUID.randomUUID().toString(),
                badTaskId, "body3", "user3");

        Comment badComment2 = new Comment(UUID.randomUUID().toString(),
                badTaskId, "body4", "user4");

        CommentRecord goodRecord1 = formatRecordFromComment(goodComment1);
        CommentRecord goodRecord2 = formatRecordFromComment(goodComment2);
        CommentRecord badRecord1 = formatRecordFromComment(badComment1);
        CommentRecord badRecord2 = formatRecordFromComment(badComment2);

        ArrayList<CommentRecord> records = new ArrayList<>();
        records.add(goodRecord1);
        records.add(goodRecord2);
        records.add(badRecord1);
        records.add(badRecord2);

        CommentResponse goodResponse1 = new CommentResponse(goodComment1.getCommentId(),
                goodComment1.getTaskId(),
                goodComment1.getCommentBody(),
                goodComment1.getUsername());

        CommentResponse goodResponse2 = new CommentResponse(goodComment2.getCommentId(),
                goodComment2.getTaskId(),
                goodComment2.getCommentBody(),
                goodComment2.getUsername());

        when(commentRepository.findAll()).thenReturn(records);

        List<CommentResponse> response = commentService.getCommentsByTaskId(goodTaskId);

        List<String> desiredComentIds = new ArrayList<>();
        desiredComentIds.add(goodComment1.getCommentId());
        desiredComentIds.add(goodComment2.getCommentId());

        for (CommentResponse indivResponse : response) {
            String responseCommentId = indivResponse.getCommentId();
            Assertions.assertTrue(desiredComentIds.contains(responseCommentId));
        }
    }

    CommentRecord formatRecordFromComment(Comment comment) {
        CommentRecord record = new CommentRecord();
        record.setCommentId(comment.getCommentId());
        record.setUsername(comment.getUsername());
        record.setCommentBody(comment.getCommentBody());
        record.setTaskId(comment.getTaskId());

        return record;
    }
}
