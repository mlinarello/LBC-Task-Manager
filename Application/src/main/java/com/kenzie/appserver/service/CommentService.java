package com.kenzie.appserver.service;

import com.kenzie.appserver.controller.model.CommentResponse;
import com.kenzie.appserver.repositories.CommentRepository;
import com.kenzie.appserver.repositories.TaskRepository;
import com.kenzie.appserver.repositories.model.CommentRecord;
import com.kenzie.appserver.service.model.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    private CommentRepository commentRepository;
    private TaskRepository taskRepository;

    public CommentService(CommentRepository commentRepository,
                          TaskRepository taskRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
    }

    public Comment createComment(Comment comment) {
        String inputTaskId = comment.getTaskId();
        String inputCommentId = comment.getCommentId();
        String inputCommentBody = comment.getCommentBody();
        String inputUsername = comment.getUsername();

        if (inputCommentId == null || inputCommentBody == null || inputUsername == null)
        {
            throw new IllegalArgumentException();
        }

        CommentRecord commentRecord = new CommentRecord();
        commentRecord.setCommentBody(inputCommentBody);
        commentRecord.setCommentId(inputCommentId);
        commentRecord.setTaskId(inputTaskId);
        commentRecord.setUsername(inputUsername);

        commentRepository.save(commentRecord);

        return comment;
    }

    public List<CommentResponse> getCommentsByTaskId(String taskId) {
        Iterable<CommentRecord> records = commentRepository.findAll();
        ArrayList<CommentResponse> responses = new ArrayList<>();
        for (CommentRecord comment :
                records) {
            if (comment.getTaskId().equals(taskId)) {
                CommentResponse commentResponse = new CommentResponse(comment.getCommentId(), comment.getTaskId(),
                        comment.getCommentBody(), comment.getUsername());
                responses.add(commentResponse);
            }
        }
        return responses;
    }

    public void deleteCommentByCommentId(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public boolean taskIdExists(String taskId) {
        return taskRepository.existsById(taskId);
    }
}
