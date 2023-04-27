package com.sprta.hanghae992.service;

import com.sprta.hanghae992.dto.MsgResponseDto;
import com.sprta.hanghae992.dto.PostRequestDto;
import com.sprta.hanghae992.dto.PostResponseDto;
import com.sprta.hanghae992.entity.Comment;
import com.sprta.hanghae992.entity.Post;
import com.sprta.hanghae992.entity.User;
import com.sprta.hanghae992.entity.UserRoleEnum;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    //게시글 작성
    @Transactional
    public ResponseEntity createPost(PostRequestDto postRequestDto, HttpServletRequest httpServletRequest) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(httpServletRequest);
        Claims claims;

        // 토큰이 있는 경우에만 게시글 추가 가능
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                MsgResponseDto msgResponseDto = new MsgResponseDto("토큰이 유효하지 않습니다", 400);
                return ResponseEntity.status(400).body(msgResponseDto);
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject());
            if (user == null) {
                MsgResponseDto msgResponseDto = new MsgResponseDto("사용자가 존재하지 않습니다", 400);
                return ResponseEntity.status(400).body(msgResponseDto);
            } else {

                Post post = postRepository.saveAndFlush(new Post(postRequestDto, user.getId(), user.getUsername()));
                return ResponseEntity.status(200).body(new PostResponseDto(post));
            }
        }MsgResponseDto msgResponseDto = new MsgResponseDto("게시글 작성 실패", 400);
        return ResponseEntity.status(400).body(msgResponseDto);

        }


    //게시글 전체 조회
    @Transactional
    public List<PostResponseDto> getPost() {
        List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
        List<PostResponseDto> postResponseDtoList = new ArrayList();
        for(Post post : postList){
            postResponseDtoList.add(new PostResponseDto(post));
        }


//        List<Post> posts = postRepository.findAllFetchJoin();
//        return posts.stream().map(PostResponseDto::new).collect(Collectors.toList());
        return postResponseDtoList;
    }

    //게시글 수정
    @Transactional
    public ResponseEntity updatePost(Long id, PostRequestDto postRequestDto, HttpServletRequest httpServletRequest) {

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
            if (user == null) {
                MsgResponseDto msgResponseDto = new MsgResponseDto("사용자가 존재하지 않습니다.", 400);
                return ResponseEntity.status(400).body(msgResponseDto);
            } else {
                //사용자 권환 확인 (ADMUN인지 아닌지)
                UserRoleEnum userRoleEnum = user.getRole();
                System.out.println("role = " + userRoleEnum);
                if (userRoleEnum == UserRoleEnum.ADMIN) {
                    Post post = postRepository.findById(id).orElseThrow(
                            () -> new NullPointerException("해당 게시글은 존재하지 않습니다.")
                    );
                    post.update(postRequestDto);
                    return ResponseEntity.status(200).body(new PostResponseDto(post));
                } else {

                    Post post = postRepository.findByIdAndUserId(id, user.getId());
                    if (post == null) {
                        MsgResponseDto msgResponseDto = new MsgResponseDto("작성자만 삭제/수정할 수 있습니다.", 400);
                        return ResponseEntity.status(400).body(msgResponseDto);
                    } else {

                        post.update(postRequestDto);
                        return ResponseEntity.status(200).body(new PostResponseDto(post));
                    }
                }
            }
        }else {
            MsgResponseDto msgResponseDto = new MsgResponseDto("게시글 수정 실패", 400);
            return ResponseEntity.status(400).body(msgResponseDto);
        }

    }

    //게시글 삭제
    @Transactional
    public ResponseEntity deleteMemo(Long id, HttpServletRequest httpServletRequest) {
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
            if (user == null) {
                MsgResponseDto msgResponseDto = new MsgResponseDto("사용자가 존재하지 않습니다.", 400);
                return ResponseEntity.status(400).body(msgResponseDto);
            } else {

                //사용자 권환 확인 (ADMUN인지 아닌지)
                UserRoleEnum userRoleEnum = user.getRole();
                System.out.println("role = " + userRoleEnum);
                if (userRoleEnum == UserRoleEnum.ADMIN) {
                    Post post = postRepository.findById(id).orElseThrow(
                            () -> new NullPointerException("해당 게시글은 존재하지 않습니다.")
                    );
                    postRepository.deleteById(id);
                    MsgResponseDto msgResponseDto = new MsgResponseDto("게시글 삭제 성공", 200);
                    return ResponseEntity.status(200).body(msgResponseDto);
                } else {
                    Post post = postRepository.findByIdAndUserId(id, user.getId());
                    if (post == null) {
                        MsgResponseDto msgResponseDto = new MsgResponseDto("작성자만 삭제/수정할 수 있습니다.", 400);
                        return ResponseEntity.status(400).body(msgResponseDto);
                    } else {
                        postRepository.deleteById(id);
                        MsgResponseDto msgResponseDto = new MsgResponseDto("게시글 삭제 성공", 200);
                        return ResponseEntity.status(200).body(msgResponseDto);
                    }
                }
            }
            } else{
            MsgResponseDto msgResponseDto = new MsgResponseDto("게시글 삭제 실패", 400);
            return ResponseEntity.status(400).body(msgResponseDto);

            }
        }




    //게시글 상세조회
    public PostResponseDto oneGetPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );
        PostResponseDto postResponseDto = new PostResponseDto(post);
        return postResponseDto;
    }
}
