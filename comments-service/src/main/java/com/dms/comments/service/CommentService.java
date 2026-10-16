package com.dms.comments.service;

import com.dms.comments.model.Comment;
import com.dms.comments.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> listByDocId(UUID docId) {
        return commentRepository.findByDocId(docId);
    }

    public Comment addComment(UUID docId, String content, String author) {
        Comment comment = new Comment(docId, content, author);
        return commentRepository.save(comment);
    }
}
