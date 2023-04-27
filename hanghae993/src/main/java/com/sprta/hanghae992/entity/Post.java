package com.sprta.hanghae992.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprta.hanghae992.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String contents;


    @Column(nullable = false)
    private Long userId;

    @Column
    private String username;

//외래키를 Post테이블이 가지고 있어야 Post테이블에서 commentList를 추가해 줄 수 있다.  (중요)
//    @JsonIgnore //무한 참조 없애기
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) //조회할때도 관련 테이블 한번에 가져오고 삭제 할때도 한번에 삭제
    @JoinColumn(name = "post_id")                                 //EAGER 즉시로딩
    private List<Comment> commentList = new ArrayList<>();






    public Post(PostRequestDto postRequestDto, Long userId, String username) {   //게시글 작성
        this.userId = userId;
        this.username = username;
        this.title = postRequestDto.getTitle();
        this.contents = postRequestDto.getContents();
    }

    public void update(PostRequestDto postRequestDto) {    //메모 수정
        this.contents = postRequestDto.getContents();
        this.title = postRequestDto.getTitle();
    }

    public void commentListadd(Comment comment) {
        this.commentList.add(comment);
    }
}
