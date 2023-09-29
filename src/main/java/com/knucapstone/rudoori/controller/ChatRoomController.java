package com.knucapstone.rudoori.controller;

import com.knucapstone.rudoori.common.ApiResponse;
import com.knucapstone.rudoori.model.dto.ChatRooms.*;
import com.knucapstone.rudoori.model.dto.ScoreRequest;
import com.knucapstone.rudoori.model.entity.ChatRoom;
import com.knucapstone.rudoori.model.entity.UserInfo;
import com.knucapstone.rudoori.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    //파일 전송
    @PostMapping()
    public ApiResponse<String> sendFile(
            @RequestPart(required = false) List<MultipartFile> multipartFile,
            @RequestParam String roomId,
            @RequestParam String userId
    ) {
        return ApiResponse.createSuccess(chatRoomService.sendFile(multipartFile, roomId, userId));
    }


    // 채팅방 생성
    @PostMapping("/create")
    public ApiResponse<RoomResponse> createRoom(@RequestBody RoomRequest request, @AuthenticationPrincipal UserInfo user) {

        RoomResponse response = chatRoomService.createRoom(request, user);


        return ApiResponse.createSuccess(response);
    }



    // 전체 방 목록 보기
    @GetMapping("/list")
    public ApiResponse<List<RoomResponse>> getRoomList()
    {
        return ApiResponse.createSuccess(chatRoomService.getRoomList());
    }

    // 채팅방 입장전 미리보기
    @GetMapping("/preview")
    public ApiResponse<RoomPreview> chatRoomPreview(@RequestParam("roomId") String roomId) {

        RoomPreview response = chatRoomService.chatRoomPreview(roomId);


        return ApiResponse.createSuccess(response);
    }

    // 메시지 생성
//    @PostMapping("/newMessage")
//    public ApiResponse<MessageResponse> sendMessage(@RequestParam("roomId") String roomId, @RequestBody MessageRequest request, @AuthenticationPrincipal UserInfo user) {
//        MessageResponse response = chatRoomService.sendMessage(request, user, roomId);
//        return ApiResponse.createSuccess(response);
//    }


    //메시지 조회
    @GetMapping("/message/{roomId}/{page}")
    public ApiResponse<List<MessageResponse>> getMessageList(@PathVariable("roomId") String roomId, @PathVariable("page") int page) {

        List<MessageResponse> response = chatRoomService.getMessageList(roomId, page);

        return ApiResponse.createSuccess(response);
    }

    @GetMapping("/pages/{roomId}")
    public int getTotalPages(@PathVariable String roomId) {
        int totalPages = chatRoomService.getTotalPages(roomId);
        return totalPages;
    }

    /**
     * 사용자가 참여하고 있는 채팅 저장된 리스트 불러오기
     * @param user
     * @return
     */
    @GetMapping("/list/user")
    public ApiResponse<List<ChatRoom>> getUserInvolved(@AuthenticationPrincipal UserInfo user){
        return ApiResponse.createSuccess(chatRoomService.getInvolvedList(user));
    }


    // 키워드로 채팅방 검색
    @PostMapping("/searchRoom")
    @ResponseBody   //json으로 바꿔줌
    public ApiResponse<List<RoomResponse>> searchRoomByKeyword(@RequestBody SearchRoomRequest request) {

        List<RoomResponse> responseList = chatRoomService.searchRoomByKeyword(request);


        return ApiResponse.createSuccess(responseList);

    }

    @GetMapping("/valid/{roomId}")
    public ApiResponse<String> checkValidEnter (@AuthenticationPrincipal UserInfo user , @PathVariable String roomId){
        System.out.println(user);

        return ApiResponse.createSuccess(chatRoomService.validEnterUser(user,roomId));

    }

    @PostMapping("/member/{roomId}")
    public ApiResponse<ChatRoom> setRoomMemberFix(@AuthenticationPrincipal UserInfo user, @PathVariable String roomId){
        return ApiResponse.createSuccess(chatRoomService.setRoomMemberFix(user,roomId));
    }

    @PostMapping("/member/score/{roomId}")
    public ApiResponse<ChatRoom> setMemberScore(@AuthenticationPrincipal UserInfo user, @PathVariable String roomId, @RequestBody ScoreRequest request){
        System.out.println("==========request==========="+request);
        return ApiResponse.createSuccess(chatRoomService.setMemberScore(user,roomId, request));
    }


}

