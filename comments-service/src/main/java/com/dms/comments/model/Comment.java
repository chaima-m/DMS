package com.dms.comments.model;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("comments")
public class Comment {

    @PrimaryKeyColumn(name = "doc_id", type = PrimaryKeyType.PARTITIONED)
    private UUID docId;

    @PrimaryKeyColumn(name = "comment_id", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private UUID commentId;

    @Column("content")
    private String content;

    @Column("author")
    private String author;

    public Comment() {}

    public Comment(UUID docId, String content, String author) {
        this.docId = docId;
        this.commentId = com.datastax.oss.driver.api.core.uuid.Uuids.timeBased();
        this.content = content;
        this.author = author;
    }

    public UUID getDocId() { return docId; }
    public void setDocId(UUID docId) { this.docId = docId; }
    public UUID getCommentId() { return commentId; }
    public void setCommentId(UUID commentId) { this.commentId = commentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}
