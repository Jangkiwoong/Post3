package com.sprta.hanghae992.service;

import com.sprta.hanghae992.dto.LoginRequestDto;
import com.sprta.hanghae992.dto.MsgResponseDto;
import com.sprta.hanghae992.dto.SignupRequestDto;
import com.sprta.hanghae992.entity.User;
import com.sprta.hanghae992.entity.UserRoleEnum;
import com.sprta.hanghae992.jwt.JwtUtil;
import com.sprta.hanghae992.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    private final JwtUtil jwtUtil;


    //회원가입
    @Transactional
    public ResponseEntity signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String password = signupRequestDto.getPassword();


        // 회원 중복 확인
        User found = userRepository.findByUsername(username);
        if (found != null) {
            MsgResponseDto msgResponseDto = new MsgResponseDto("중복된 사용자가 존재합니다.", 400);
            return ResponseEntity.status(400).body(msgResponseDto);
        } else {

            // 사용자 ROLE 확인
            UserRoleEnum role = UserRoleEnum.USER;
            if (signupRequestDto.getAdminToken() != "") {
                if (!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                    MsgResponseDto msgResponseDto = new MsgResponseDto("관리자 암호가 틀려 등록이 불가능합니다.", 400);
                    return ResponseEntity.status(400).body(msgResponseDto);
                }else {
                    role = UserRoleEnum.ADMIN;
                }
            }

            //^[a-zA-Z0-9\d@$!%*#?&]{8,15}$(정규식)
            //^[a-zA-Z\p{Punct}0-9]*$(함수)
            if (!Pattern.matches("^[a-z0-9]{4,10}$", username) || !Pattern.matches("^[a-zA-Z0-9\\d@$!%*#?&]{8,15}$", password)) {
                throw new IllegalArgumentException("양식 오류.");
            }

            User user = new User(username, password, role);
            userRepository.save(user);
            MsgResponseDto msgResponseDto = new MsgResponseDto("회원가입 성공", 200);
            return ResponseEntity.status(HttpStatus.OK).body(msgResponseDto);
        }
    }


    //로그인
    @Transactional(readOnly = true)
    public ResponseEntity login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();


        // 사용자 확인

        User user = userRepository.findByUsername(username);
        if(user == null) {
            MsgResponseDto msgResponseDto = new MsgResponseDto("회원을 찾을 수 없습니다", 400);
            return ResponseEntity.status(400).body(msgResponseDto);
        }
        if(!user.getPassword().equals(password)) {
            MsgResponseDto msgResponseDto = new MsgResponseDto("회원을 찾을 수 없습니다", 400);
            return ResponseEntity.status(400).body(msgResponseDto);
        }

        // 비밀번호 확인
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.getUsername(), user.getRole()));

       MsgResponseDto msgResponseDto = new MsgResponseDto("로그인 성공", 200);
        return ResponseEntity.status(HttpStatus.OK).body(msgResponseDto);
        }
    }

