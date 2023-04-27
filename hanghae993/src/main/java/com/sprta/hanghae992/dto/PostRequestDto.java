package com.sprta.hanghae992.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
public class PostRequestDto {

    private String title;
    private String contents;
}
