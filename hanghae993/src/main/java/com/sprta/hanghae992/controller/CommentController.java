package com.sprta.hanghae992.controller;


import com.sprta.hanghae992.dto.CmtRequestDto;
import com.sprta.hanghae992.dto.CmtResponseDto;
import com.sprta.hanghae992.dto.MsgResponseDto;
import com.sprta.hanghae992.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 작성
    @PostMapping("/comment")
    public ResponseEntity addComment(@RequestBody CmtRequestDto cmtRequestDto, HttpServletRequest httpServletRequest){
        return commentService.addComment(cmtRequestDto, httpServletRequest);
    }

    //댓글 수정
    @PutMapping("/comment/{id}")
    public ResponseEntity updateComment(@PathVariable Long id, @RequestBody CmtRequestDto cmtRequestDto, HttpServletRequest httpServletRequest) {
        return commentService.updateComment(id, cmtRequestDto, httpServletRequest);
    }


    //댓글 삭제
    @DeleteMapping("/comment/{id}")
    public ResponseEntity deleteComment(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        return commentService.deleteComment(id, httpServletRequest);
    }
}
