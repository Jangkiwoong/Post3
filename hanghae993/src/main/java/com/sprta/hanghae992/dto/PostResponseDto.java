package com.sprta.hanghae992.dto;

import com.sprta.hanghae992.entity.Comment;
import com.sprta.hanghae992.entity.Post;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponseDto {

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long id;
    private String title;
    private String contents;
    private String username;
    private List<Comment> commentList;


    public PostResponseDto(Post post) {
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.id = post.getId();
        this.title = post.getTitle();
        this.contents = post.getContents();
        this.username = post.getUsername();
        this.commentList = post.getCommentList();
    }
}
