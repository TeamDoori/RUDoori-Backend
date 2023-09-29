package com.knucapstone.rudoori.controller;

import com.knucapstone.rudoori.common.ApiResponse;
import com.knucapstone.rudoori.model.dto.Board.BoardRequest;
import com.knucapstone.rudoori.model.dto.Board.BoardResponse;
import com.knucapstone.rudoori.model.dto.ReplyDto;
import com.knucapstone.rudoori.model.dto.ScrapResponse;
import com.knucapstone.rudoori.model.entity.Posts;
import com.knucapstone.rudoori.model.entity.UserInfo;
import com.knucapstone.rudoori.repository.UserRepository;
import com.knucapstone.rudoori.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final UserRepository userRepository;

    @GetMapping("/test")
    public void createRoomFolder() {
        String folderPath = "/Users/baghyeong-u/Desktop/imageFolder/" + "test";
        File folder = new File(folderPath);

        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                System.out.println("폴더 생성 성공: " + folderPath);
            } else {
                System.err.println("폴더 생성 실패: " + folderPath);
            }
        } else {
            System.out.println("이미 폴더가 존재합니다: " + folderPath);
        }
    }
    @GetMapping("/list")
    public ApiResponse<Slice<BoardResponse>> getBoardList(
            @PageableDefault(sort = "postId", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        Slice<BoardResponse> boardList = boardService.getBoardList(pageable);
        return ApiResponse.createSuccess(boardList);
    }
    //검색
    @GetMapping("/search")
    public ApiResponse<Slice<BoardResponse>> searchBoard(@RequestParam(required = false) String keyWord,
                                                        @PageableDefault(sort = "postId", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        return ApiResponse.createSuccess(boardService.searchBoard(keyWord, pageable));
    }

    @GetMapping("/myBoardList/{userId}")
    public ApiResponse<Slice<BoardResponse>> getMyBoard(@PathVariable String userId, @PageableDefault(sort = "postId", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {

        return ApiResponse.createSuccess(boardService.getMyboard(userId, pageable));
    }

    //    @GetMapping("/list/getPostId")
//    public Long getLastPostId(
//    ){
//        return boardService.getLastPostId();
//    }

    @PostMapping()
    public ApiResponse<Long> createBoard(
            @RequestPart(required = false) List<MultipartFile> multipartFile,
            @RequestParam String content,
            @RequestParam String title,
            @AuthenticationPrincipal UserInfo userinfo
    ) {
        return ApiResponse.createSuccess(boardService.createBoard(multipartFile, content, title, userinfo));
    }



    @GetMapping()
    public ApiResponse<BoardResponse> getBoard(@RequestParam Long boardId) {
        return ApiResponse.createSuccess(boardService.getBoard(boardId));
    }

    @PatchMapping("/{boardId}")
    public ApiResponse<BoardResponse> updateBoard(
            @PathVariable("boardId") Long boardId,
            @RequestBody BoardRequest boardRequest,
            @AuthenticationPrincipal UserInfo userinfo
    ) throws Exception {
        return ApiResponse.createSuccess(boardService.updateBoard(boardId, boardRequest, userinfo));
    }

    @DeleteMapping()
    public ApiResponse<Boolean> deleteBoard(
            @RequestParam Long boardId,
            @AuthenticationPrincipal UserInfo userinfo
    ) {
        return ApiResponse.createSuccess(boardService.deleteBoard(boardId, userinfo));
    }

    @PostMapping("/{boardId}/reply/parent")
    public ApiResponse<ReplyDto.CreateReplyResponse> createParentReply
            (@PathVariable("boardId") Long boardId,
             @RequestBody ReplyDto.CreateReplyRequest request,
             @AuthenticationPrincipal UserInfo userInfo) {

        return ApiResponse.createSuccess(boardService.createParentReply(boardId, userInfo, request));
    }

    @PostMapping("/{boardId}/reply/{parentId}/child")
    public ApiResponse<ReplyDto.CreateChildrenReplyResponse> createChildReply
            (@PathVariable("boardId") Long boardId,
             @PathVariable("parentId") Long parentId,
             @AuthenticationPrincipal UserInfo userInfo,
             @RequestBody ReplyDto.CreateReplyRequest request) {
        return ApiResponse.createSuccess(boardService.createChildReply(boardId, parentId, userInfo, request));
    }

    @DeleteMapping("/comment")
    public ApiResponse<Boolean> deleteCommentBoard(
            @RequestParam("commentId") Long commentId,
            @AuthenticationPrincipal UserInfo userinfo
    ) {
        return ApiResponse.createSuccess(boardService.deleteCommentBoard(commentId, userinfo));
    }

    // 스크랩------------------------------------------------------------------------------
    @PostMapping("/scrap/{boardId}")
    public ApiResponse<ScrapResponse> createScrapBoard(
            @AuthenticationPrincipal UserInfo userinfo,
            @PathVariable("boardId") Long boardId
    ) {
        return ApiResponse.createSuccess(boardService.createScrapBoard(boardId, userinfo));
    }
    @DeleteMapping("/scrap/{boardId}")
    public ApiResponse<Boolean> deleteScrapBoard(
            @AuthenticationPrincipal UserInfo userinfo,
            @PathVariable("boardId") Long boardId
    ) {
        return ApiResponse.createSuccess(boardService.deleteScrapBoard(boardId, userinfo));
    }
    @GetMapping("/scrap")
    public ApiResponse<List<ScrapResponse>> getScrapBoard(
            @AuthenticationPrincipal UserInfo userinfo
    ) {
        return ApiResponse.createSuccess(boardService.getScrapBoard(userinfo));
    }

}
