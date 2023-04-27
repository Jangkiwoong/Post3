package com.sprta.hanghae992.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
public class SignupRequestDto {
    //null, size, pattern순서로 넣어줘야 함
    private String username;
    private String password;
//    private boolean admin = false;   //항상 false인데..?
    private String adminToken = "";
}