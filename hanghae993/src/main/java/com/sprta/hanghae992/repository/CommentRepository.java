package com.sprta.hanghae992.repository;

import com.sprta.hanghae992.entity.Comment;
import com.sprta.hanghae992.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByIdAndUsername(Long id, String username);

}
