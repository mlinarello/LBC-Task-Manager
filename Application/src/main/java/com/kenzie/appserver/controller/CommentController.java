package com.kenzie.appserver.controller;

import com.kenzie.appserver.controller.model.CommentCreateRequest;
import com.kenzie.appserver.controller.model.CommentDeleteRequest;
import com.kenzie.appserver.controller.model.CommentResponse;
import com.kenzie.appserver.service.CommentService;
import com.kenzie.appserver.service.model.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private CommentService commentService;

    CommentController(CommentService commentService) {
        this.commentService = commentService;

    }

    @PostMapping("/create")
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentCreateRequest commentCreateRequest) {
        if (!commentService.taskIdExists(commentCreateRequest.getTaskId())) {
            return ResponseEntity.badRequest().build();
        }

        Comment comment = new Comment(UUID.randomUUID().toString(), commentCreateRequest.getTaskId(),
                commentCreateRequest.getCommentBody(), commentCreateRequest.getUsername());
        try {
            commentService.createComment(comment);
        }
        catch (IllegalArgumentException iae) {
            return ResponseEntity.badRequest().build();
        }

        CommentResponse response = createCommentResponse(comment);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTaskId(@PathVariable String taskId) {
        if (!commentService.taskIdExists(taskId)) {
            return ResponseEntity.notFound().build();
        }

        List<CommentResponse> commentResponses = commentService.getCommentsByTaskId(taskId);
        return ResponseEntity.ok(commentResponses);
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteCommentByCommentId(@PathVariable String commentId) {
        commentService.deleteCommentByCommentId(commentId);
        return ResponseEntity.ok(commentId);
    }

    private CommentResponse createCommentResponse(Comment comment) {

        return new CommentResponse(comment.getCommentId(),
                comment.getTaskId(),
                comment.getCommentBody(),
                comment.getUsername());

    }
}
