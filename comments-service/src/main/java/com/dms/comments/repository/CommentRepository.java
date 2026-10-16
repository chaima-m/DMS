package com.dms.comments.repository;

import com.dms.comments.model.Comment;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends CassandraRepository<Comment, UUID> {
    List<Comment> findByDocId(UUID docId);
}
