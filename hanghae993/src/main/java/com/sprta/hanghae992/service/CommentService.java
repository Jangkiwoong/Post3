package com.sprta.hanghae992.service;


import com.sprta.hanghae992.dto.*;
import com.sprta.hanghae992.entity.*;
import com.sprta.hanghae992.jwt.JwtUtil;
import com.sprta.hanghae992.repository.CommentRepository;
import com.sprta.hanghae992.repository.PostRepository;
import com.sprta.hanghae992.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {


    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JwtUtil jwtUtil;


    //댓글 추가
    @Transactional
    public ResponseEntity addComment(CmtRequestDto cmtRequestDto, HttpServletRequest httpServletRequest) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(httpServletRequest);
        Claims claims;

        // 토큰이 있는 경우에만 댓글 추가 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                MsgResponseDto msgResponseDto = new MsgResponseDto("토큰이 유효하지 않습니다.", 400);
                return ResponseEntity.status(400).body(msgResponseDto);
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject());
            if(user == null) {
                MsgResponseDto msgResponseDto = new MsgResponseDto("사용자가 존재하지 않습니다.", 400);
                return ResponseEntity.status(400).body(msgResponseDto);
            } else {

                Post post = postRepository.findById(cmtRequestDto.getPostId()).orElseThrow(
                        () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
                );


                Comment comment = commentRepository.saveAndFlush(new Comment(cmtRequestDto, user.getUsername()));
                post.commentListadd(comment);
                CmtResponseDto cmtResponseDto = new CmtResponseDto(comment);
                return ResponseEntity.status(200).body(cmtResponseDto);
            }
        }else {
            MsgResponseDto msgResponseDto = new MsgResponseDto("댓글 작성 실패", 400);
            return ResponseEntity.status(400).body(msgResponseDto);
        }
    }


    //댓글 수정
    @Transactional
    public ResponseEntity updateComment(Long id, CmtRequestDto cmtRequestDto, HttpServletRequest httpServletRequest) {

        String token = jwtUtil.resolveToken(httpServletRequest);
        Claims claims;

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                MsgResponseDto msgResponseDto = new MsgResponseDto("토큰이 유효하지 않습니다.", 400);
                return ResponseEntity.status(400).body(msgResponseDto);
            }

            User user = userRepository.findByUsername(claims.getSubject());
            if(user == null) {
                MsgResponseDto msgResponseDto = new MsgResponseDto("사용자가 존재하지 않습니다.", 400);
                return ResponseEntity.status(400).body(msgResponseDto);
            } else {

            //사용자 권환 확인 (ADMUN인지 아닌지)
            UserRoleEnum userRoleEnum = user.getRole();
            System.out.println("role = " + userRoleEnum);
            if( userRoleEnum == UserRoleEnum.ADMIN) {
                Comment comment = commentRepository.findById(id).orElseThrow(
                        () -> new NullPointerException("해당 게시글은 존재하지 않습니다.")
                );
                comment.update(cmtRequestDto);
                CmtResponseDto cmtResponseDto = new CmtResponseDto(comment);
                return ResponseEntity.status(200).body(cmtResponseDto);
            }else {


                Comment comment = commentRepository.findByIdAndUsername(id, user.getUsername());
                if (comment == null) {
                    MsgResponseDto msgResponseDto = new MsgResponseDto("작성자만 삭제/수정할 수 있습니다.", 400);
                    return ResponseEntity.status(400).body(msgResponseDto);
                } else {
                    comment.update(cmtRequestDto);
                    CmtResponseDto cmtResponseDto = new CmtResponseDto(comment);
                    return ResponseEntity.status(200).body(cmtResponseDto);
                }
            }
            }
        }else {
            MsgResponseDto msgResponseDto = new MsgResponseDto("댓글 수정 실패", 400);
            return ResponseEntity.status(400).body(msgResponseDto);
        }
    }
//

    //댓글 삭제
    @Transactional
    public ResponseEntity deleteComment(Long id, HttpServletRequest httpServletRequest) {
        String token = jwtUtil.resolveToken(httpServletRequest);
        Claims claims;

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                MsgResponseDto msgResponseDto = new MsgResponseDto("토큰이 유효허자 않습니다.", 400);
                return ResponseEntity.status(400).body(msgResponseDto);
            }

            User user = userRepository.findByUsername(claims.getSubject());
            if(user == null) {
                MsgResponseDto msgResponseDto = new MsgResponseDto("사용자가 존재하지 않습니다.", 400);
                return ResponseEntity.status(400).body(msgResponseDto);
            } else {

            //사용자 권환 확인 (ADMUN인지 아닌지)
            UserRoleEnum userRoleEnum = user.getRole();
            System.out.println("role = " + userRoleEnum);
            if( userRoleEnum == UserRoleEnum.ADMIN) {
                Comment comment = commentRepository.findById(id).orElseThrow(
                        () -> new NullPointerException("해당 게시글은 존재하지 않습니다.")
                );
                commentRepository.deleteById(id);
                MsgResponseDto msgResponseDto = new MsgResponseDto("댓글 삭제 성공", 200);
                return ResponseEntity.status(200).body(msgResponseDto);
            }else {

                Comment comment = commentRepository.findByIdAndUsername(id, user.getUsername());
                if (comment == null) {
                    MsgResponseDto msgResponseDto = new MsgResponseDto("작성자만 삭제/수정할 수 있습니다.", 400);
                    return ResponseEntity.status(400).body(msgResponseDto);
                } else {
                commentRepository.deleteById(id);
                MsgResponseDto msgResponseDto = new MsgResponseDto("댓글 삭제 성공", 200);
                return ResponseEntity.status(200).body(msgResponseDto);
            }}}

        } else {
            MsgResponseDto msgResponseDto = new MsgResponseDto("댓글 삭제 실패", 400);
            return ResponseEntity.status(400).body(msgResponseDto);
        }

    }

}
