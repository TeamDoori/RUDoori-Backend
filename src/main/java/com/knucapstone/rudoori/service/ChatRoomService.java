package com.knucapstone.rudoori.service;

import com.google.gson.Gson;
import com.knucapstone.rudoori.model.dto.ChatRooms.*;
import com.knucapstone.rudoori.model.dto.ScoreRequest;
import com.knucapstone.rudoori.model.entity.*;
import com.knucapstone.rudoori.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository messageRepository;
    private final MongoDBTemplate mongoDBTemplate;
    private final ChatInvolveRepository involveRepository;
    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;
    private final ChatSessionRepository chatSessionRepository;
    // 채팅방 생성
    private final ImageRepository imageRepository;

    @Value("${file.serverDir}")
    private String serverFileDir;
    @Value("${file.dbDir}")
    private String dbFileDir;

    //파일 전송
    @Transactional
    public String sendFile(List<MultipartFile> multipartFile, String roomId, String userId) {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            if (multipartFile.size() > 0) {

                String folderPath = "/home/devuser/Doori/image/chat/" + roomId;
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
            multipartFile.forEach(files -> {
                String origName = files.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                String savedName = uuid + origName.substring(origName.lastIndexOf("."));
                String savedServerPath = serverFileDir + "chat/" + roomId + "/" + savedName;
                String savedDbPath = dbFileDir + "chat/" + roomId + "/" + savedName;

//                String savedServerPath = serverFileDir + savedName;
//                String savedDbPath = dbFileDir + savedName;

                Image file = Image.builder()
                        .uploadFileName(origName)
                        .storeFileName(savedName)
                        .path(savedDbPath)
                        .roomId(roomId)
                        .userId(userId)
                        .build();

                try {
                    files.transferTo(new File(savedServerPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageRepository.save(file);

            });
        }
            return roomId;
        }
        // 채팅방 생성

    @Transactional
    public RoomResponse createRoom(RoomRequest request, UserInfo user) {

        Set<ChatRoom.User> participants = new HashSet<>();
        String id = UUID.randomUUID().toString();
        ChatRoom.User participant = new ChatRoom.User();
        participant.setName(user.getNickname());
        participant.set_id(user.getUserId());
        ChatRoom.User host = new ChatRoom.User();
        host.set_id(user.getUserId());
        host.setName(user.getNickname());
//        host.setAvatar(user.getAvatar());

//        participant.setAvatar(user.getA); //유저 사진
        participants.add(participant);     // 방장아이디 참여자로 등록


        ChatRoom chatRoom = ChatRoom.builder()
                ._id(id)
                .participants(participants)
                .hostUser(host)
                .roomName(request.getRoomName())
                .createdAt(LocalDateTime.now())
                .introduce(request.getIntroduce())
                .maxParticipants(request.getMaxParticipants())
                .blockedMember(new HashSet<>())
                .category(request.getCategory())
                .isFull(false)
                .isUsed(true)
                .isComplete(false)
                .completeCounter(0)
                .build();


        chatRoomRepository.insert(chatRoom);
        Optional<UserInvolve> involve = involveRepository.findById(user.getUserId());
        UserInvolve userInvolve;
        if (involve.isEmpty()) {
            userInvolve = new UserInvolve();
            userInvolve.setId(user.getUserId());
        } else {
            userInvolve = involve.get();
        }
        userInvolve.getChatRoomIds().add(chatRoom.get_id());
        involveRepository.save(userInvolve);


        RoomResponse response = RoomResponse.builder()
                ._id(chatRoom.get_id())
                .roomName(chatRoom.getRoomName())
                .hostUser(chatRoom.getHostUser())
                .createdAt(chatRoom.getCreatedAt())
                .participants(chatRoom.getParticipants())
                .blockedMember(chatRoom.getBlockedMember())
                .maxParticipants(chatRoom.getMaxParticipants())
                .introduce(chatRoom.getIntroduce())
                .category(chatRoom.getCategory())
                .isCompleted(chatRoom.isComplete())
                .isFull(chatRoom.isFull())
                .isUsed(chatRoom.isUsed())
                .build();

        return response;

    }

    // 전체 방 목록 보기
    public List<RoomResponse> getRoomList() {

        List<ChatRoom> rooms = chatRoomRepository.findAll();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (ChatRoom r : rooms) {
            roomResponses.add(RoomResponse.builder()
                    .hostUser(r.getHostUser())
                    .roomName(r.getRoomName())
                    ._id(r.get_id())
                    .createdAt(r.getCreatedAt())
                    .introduce(r.getIntroduce())
                    .maxParticipants(r.getMaxParticipants())
                    .participants(r.getParticipants())
                    .blockedMember(r.getBlockedMember())
                    .category(r.getCategory())
                    .isUsed(r.isUsed())
                    .isFull(r.isFull())
                    .isCompleted(r.isComplete())
                    .build());
        }
        return roomResponses;
    }

    private ChatMessage.Message convertToChatMessage(Chat.Message.MessageContent messageContent) {
        return ChatMessage.Message.builder()
                ._id(messageContent.getMessageId())
                .text(messageContent.getText())
                .createdAt(messageContent.getCreatedAt())
                .build();
    }

    public void saveSendMessage(Chat.Message.MessageContent messageContent, String roomId) {
        ChatMessage.Message chatMessage = convertToChatMessage(messageContent);
        chatMessage.setUser(new ChatMessage.Message.User());
        chatMessage.getUser().set_id(messageContent.getUser().getUserId());
        chatMessage.getUser().setAvatar(messageContent.getUser().getAvatarUrl());
        chatMessage.getUser().setName(messageContent.getUser().getName());

        ChatMessage message = ChatMessage.builder()
                .chatRoomId(roomId)
                .messageContent(chatMessage)
                .build();

        messageRepository.insert(message);
    }
//
//    // 채팅 메시지 생성
//    public void saveSendMessage(Chat.Message.MessageContent messageContent, String roomId) {
//
//
//        ChatMessage message = ChatMessage.builder()
//                .chatRoomId(roomId)
//                .build();
//
//        message.getMessageContent().builder()
//                ._id(message.get_id())
//                        .createdAt(messageContent.getCreatedAt())
//                                .text(messageContent.getText().build();
//
//
//
//        messageRepository.insert(message);
//
//    }

    // 채팅방 입장전 미리보기
    public RoomPreview chatRoomPreview(String roomId) {

        Optional<ChatRoom> room = chatRoomRepository.findById(roomId);

        RoomPreview response = RoomPreview.builder()
                ._id(room.get().get_id())
                .roomName(room.get().getRoomName())
                .introduce(room.get().getIntroduce())
                .maxParticipants(room.get().getMaxParticipants())
                .createdAt(room.get().getCreatedAt())
                .build();

        return response;
    }

    // 채팅방 입장완료
    public List<MessageResponse> getMessageList(String chatRoomId, int page) {

//        Optional<ChatRoom> room = chatRoomRepository.findById(chatRoomId);
//        List<ChatMessage> messageList = messageRepository.findAllByChatRoomId(chatRoomId);
        List<ChatMessage> messageList = mongoDBTemplate.getChatMessagesByRoomId(chatRoomId, page);
//        int totalPages = mongoDBTemplate.getTotalPages(chatRoomId);
//        mongoDBTemplate.getChatMessagesByRoomId(chatRoomId,totalPages);
        List<MessageResponse> messageResponses = new ArrayList<>();     // 메시지 목록

        for (ChatMessage m : messageList) {
//            messageResponses.add(gson.fromJson(m.getMessageContent(), MessageResponse.class));
            messageResponses.add(MessageResponse.builder()
                    ._id(m.getMessageContent().get_id())
                    .text(m.getMessageContent().getText())
                    .createdAt(m.getMessageContent().getCreatedAt())
                    .user(new MessageResponse.User(m.getMessageContent().getUser().get_id(), m.getMessageContent().getUser().getName(), m.getMessageContent().getUser().getAvatar()))
                    .build());

        }
        return messageResponses;
    }

    /**
     * 방 입장 후 정보 최신화
     *
     * @param roomId
     * @return
     */
    public Set<ChatRoom.User> enterRoom(String roomId) {
//        Optional<UserInvolve> involved = involveRepository.findById(userInfo.getUserId());
        ChatRoom room = chatRoomRepository.findAllBy_id(roomId);
        return room.getParticipants();
    }

    public void exitRoom(ChatSystem.Message.User messageContent, String roomId) {

        ChatRoom room = chatRoomRepository.findAllBy_id(roomId);
        ChatRoom.User user = new ChatRoom.User();
        user.set_id(messageContent.getUserId());
        user.setAvatar(messageContent.getAvatarUrl());
        user.setName(messageContent.getName());
        room.getParticipants().remove(user);
        chatRoomRepository.save(room);


        //입장하면서 방 정보 최신화 해줌
//        ChatRoom.User user = new ChatRoom.User();
//        //유저가 들어가 있는 방 정보 저장
//        user.set_id(userInfo.getUserId());
//        user.setName(userInfo.getName());
////        user.setAvatar(userInfo.getAvatarUrl());  //profile 사진
//        room.getParticipants().add(user);
//
//        UserInvolve userInvolved = involveRepository.findById(user.get_id()).orElseGet(
//                () -> UserInvolve.builder()
//                        .id(user.get_id())
//                        .chatRoomIds(new HashSet<>())
//                        .build()
//        );
//        userInvolved.getChatRoomIds().add(roomId);
//        if (room.getParticipants().size() == room.getMaxParticipants()) {
//            room.setFull(true);
//        }
//        involveSave(userInfo.getUserId(), roomId);      //
//        involveRepository.save(userInvolved);
//        chatRoomRepository.save(room);
    }

    /**
     * 유저가 포함된 채팅방 목록 불러오기
     *
     * @param user
     * @return
     */
    public List<ChatRoom> getInvolvedList(UserInfo user) {
        String id = user.getUserId();
        return mongoDBTemplate.getChatRoomsByUserId(id);

    }

    public int getTotalPages(String roomId) {
        return mongoDBTemplate.getTotalPages(roomId);
    }

    // 키워드로 채팅방 검색
    public List<RoomResponse> searchRoomByKeyword(SearchRoomRequest request) {
        String[] keywords = request.getKeywordList();
        String category = request.getCategory();
        List<ChatRoom> result = chatRoomRepository.searchByKeywordAndCategory(keywords, category);

        List<RoomResponse> responseList = new ArrayList<>();
        for (ChatRoom c : result) {
            RoomResponse response = RoomResponse.builder()
                    ._id(c.get_id())
                    .roomName(c.getRoomName())
                    .isFull(c.isFull())
                    .isCompleted(c.isComplete())
                    .isUsed(c.isUsed())
                    .hostUser(c.getHostUser())
                    .participants(c.getParticipants())
                    .introduce(c.getIntroduce())
                    .maxParticipants(c.getMaxParticipants())
                    .category(c.getCategory())
                    .blockedMember(c.getBlockedMember())
                    .createdAt(c.getCreatedAt())
                    .build();

            responseList.add(response);
        }

        return responseList;
    }

    private void involveSave(String userId, String roomId) {
        Optional<UserInvolve> involve = involveRepository.findById(userId);
        UserInvolve userInvolve;
        if (involve.isEmpty()) {
            userInvolve = new UserInvolve();
            userInvolve.setId(userId);
        } else {
            userInvolve = involve.get();
        }
        userInvolve.getChatRoomIds().add(roomId);
        involveRepository.save(userInvolve);
    }

    public String validEnterUser(UserInfo userInfo, String roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(RuntimeException::new);

        if(!room.isUsed()){
            return "finish";
        }
        for (String blocked : room.getBlockedMember()
        ) {
            if (blocked.equals(userInfo.getUserId())) {
                return "blocked";
            }
        }
        for (ChatRoom.User participant : room.getParticipants()) {
            if (participant.get_id().equals(userInfo.getUserId())) {
                return "enter";
            }
        }

        if (room.isFull() || room.isComplete() || !(room.isUsed())) {
            return "false";
        } else {
            //입장하면서 방 정보 최신화 해줌
            ChatRoom.User user = new ChatRoom.User();
            //유저가 들어가 있는 방 정보 저장
            user.set_id(userInfo.getUserId());
            user.setName(userInfo.getName());
//        user.setAvatar(userInfo.getAvatarUrl());  //profile 사진
            room.getParticipants().add(user);

            UserInvolve userInvolved = involveRepository.findById(user.get_id()).orElseGet(
                    () -> UserInvolve.builder()
                            .id(user.get_id())
                            .chatRoomIds(new HashSet<>())
                            .build()
            );
            userInvolved.getChatRoomIds().add(roomId);
            if (room.getParticipants().size() == room.getMaxParticipants()) {
                room.setFull(true);
            }
            involveSave(userInfo.getUserId(), roomId);      //
            involveRepository.save(userInvolved);
            chatRoomRepository.save(room);
        }

        return "enter";
    }

    public void blockUser(BlockUser blockUser) {
        ChatRoom room = chatRoomRepository.findById(blockUser.getRoomId()).orElseThrow(RuntimeException::new);

        for (String list : blockUser.getMessage()) {
            room.getParticipants().removeIf(participant -> participant.get_id().equals(list));
            UserInvolve involve = involveRepository.findById(list).orElseThrow(NullPointerException::new);
            involve.getChatRoomIds().remove(blockUser.getRoomId());
        }
        room.getBlockedMember().addAll(blockUser.getMessage());
        if (room.getParticipants().size() != room.getMaxParticipants()) {
            room.setFull(false);
        }
        chatRoomRepository.save(room);
    }

    public ChatRoom setRoomMemberFix(UserInfo user, String roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(RuntimeException::new);
        room.setComplete(true);
        return chatRoomRepository.save(room);
    }

    @Transactional
    public ChatRoom setMemberScore(UserInfo user, String roomId, ScoreRequest request) {
        String roomName = request.getRoomName();
        List<Score> scoreList = new ArrayList<>();

        //점수 저장
        for(Map<String , String> score : request.getScoreList()){
            if(!score.isEmpty()) {
                UserInfo userInfo = userRepository.findByUserId(score.get("opponentId")).orElseThrow(()-> new NullPointerException("사용자 정보가 없습니다."));

                double memberScore = Double.parseDouble(score.get("score"));
                double totalScore = scoreRepository.sumOfGradesByUserId(userInfo.getUserId()).orElse(0.0);
                long total = scoreRepository.countByUserId(userInfo.getUserId()).orElse(0L) +1;

                totalScore += memberScore;
                double average = totalScore/total;
                userInfo.setScore(average);
                Score s = Score.builder()
                        .opponentId(user.getUserId())
                        .grade(memberScore)
                        .mention(score.get("mention"))
                        .roomName(roomName)
                        .userId(userInfo).build();
                scoreList.add(s);
                userRepository.save(userInfo);
            }
        }
        scoreRepository.saveAll(scoreList);

        //점수를 준 이후 현재 내가 참여한 방의 정보를 제거
        UserInvolve userInvolve = involveRepository.findById(user.getUserId()).orElseThrow(NullPointerException::new);
        userInvolve.getChatRoomIds().remove(roomId);

        //방 정보 최신화
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(NullPointerException::new);

        //방 사용 정지
        if(chatRoom.isUsed()){
            chatRoom.setUsed(false);
        }

        int memberSize = chatRoom.getParticipants().size();
        int counter = chatRoom.getCompleteCounter()+1;      //점수주기를 완료하고 방을 빠져나간 인원들의 카운터
        chatRoom.setCompleteCounter(counter);
        //모든 인원이 점수 주기 입력 완료 시
        if(memberSize == counter){
            //해당 방에 있는 채팅 내역을 모두 제거 후 채팅방 정보 제거
            messageRepository.deleteAll(messageRepository.findAllByChatRoomId(roomId));
            chatRoomRepository.delete(chatRoom);
        }
        chatRoomRepository.save(chatRoom);
        involveRepository.save(userInvolve);
        return chatRoom;
    }
}

