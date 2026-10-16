package com.dms.comments.controller;

import com.dms.comments.model.Comment;
import com.dms.comments.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // GET /comments/list/{docId}
    @GetMapping("/list/{docId}")
    public ResponseEntity<List<Comment>> listComments(@PathVariable UUID docId) {
        return ResponseEntity.ok(commentService.listByDocId(docId));
    }

    // POST /comments/add
    // Body: { "docId": "uuid-here", "content": "...", "author": "..." }
    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestBody Map<String, String> body) {
        UUID docId = UUID.fromString(body.get("docId"));
        String content = body.get("content");
        String author = body.get("author");
        return ResponseEntity.ok(commentService.addComment(docId, content, author));
    }
}
