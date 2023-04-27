package com.sprta.hanghae992.dto;

import com.sprta.hanghae992.entity.Comment;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
public class CmtRequestDto {
    private Long postId;
    private String content;

}
