package com.sprta.hanghae992.entity;


import com.sprta.hanghae992.dto.CmtRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Comment extends Timestamped{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;


    @Column
    private String username;




    public Comment(CmtRequestDto cmtRequestDto, String username) { //댓글 작성

        this.username = username;
        this.content = cmtRequestDto.getContent();
    }

    public void update(CmtRequestDto cmtRequestDto) {
        this.content = cmtRequestDto.getContent();

    }
}
