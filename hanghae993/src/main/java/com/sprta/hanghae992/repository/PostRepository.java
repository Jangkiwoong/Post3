package com.sprta.hanghae992.repository;

import com.sprta.hanghae992.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long> {
      List<Post> findAllByOrderByModifiedAtDesc();  //전체조회
      Post findByIdAndUserId(Long id, Long userId);   //수정



}
