package com.knucapstone.rudoori.service;

import com.knucapstone.rudoori.common.ApiResponse;
import com.knucapstone.rudoori.model.dto.ReplyDto;
import com.knucapstone.rudoori.model.dto.ScrapResponse;
import com.knucapstone.rudoori.model.entity.*;
import com.knucapstone.rudoori.repository.*;
import com.knucapstone.rudoori.model.dto.Board.BoardRequest;
import com.knucapstone.rudoori.model.dto.Board.BoardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardJpaRepository boardJpaRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final ScrapRepository scrapRepository;
    private final ImageRepository imageRepository;

    @Value("${file.serverDir}")
    private String serverFileDir;
    @Value("${file.dbDir}")
    private String dbFileDir;


    public Page<BoardResponse> getMyboard(String userId, Pageable pageable) {
        Page<Posts> page = boardRepository.findAllByUserId(userRepository.findByUserId(userId).get(), pageable);

        Page<BoardResponse> toMap = page.map(m ->
                new BoardResponse(m.getPostId(), m.getTitle(), m.getContent(),
                        m.getUserId().getNickname(), m.getScrap(), m.getCreatedDt(), imageRepository.findAllByPost(boardRepository.findById(m.getPostId()).get()), m.getUserId().getUserId())
        );
        return toMap;
    }


    public Page<BoardResponse> searchBoard(String keyWord, Pageable pageable) {

        Page<Posts> page = boardRepository.findAllByTitleContainingOrContentContaining(keyWord, keyWord, pageable);
        Page<BoardResponse> toMap = page.map(m ->
                new BoardResponse(m.getPostId(), m.getTitle(), m.getContent(),
                        m.getUserId().getNickname(), m.getScrap(), m.getCreatedDt(), imageRepository.findAllByPost(boardRepository.findById(m.getPostId()).get()), m.getUserId().getUserId())
        );
        return toMap;
    }

    public Page<BoardResponse> getBoardList(Pageable pageable) {

//        Sort sort = Sort.by(Sort.Direction.DESC, "postId");
//        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Posts> page = boardRepository.findAll(pageable);
        Page<BoardResponse> toMap = page.map(m ->
                new BoardResponse(m.getPostId(), m.getTitle(), m.getContent(),
                        m.getUserId().getNickname(), m.getScrap(), m.getCreatedDt(), imageRepository.findAllByPost(boardRepository.findById(m.getPostId()).get()), m.getUserId().getUserId())
        );

        return toMap;
    }
//        public List<BoardResponse> getBoardList(int page) {
//       System.out.println("page: "+page);
//        int size = 15;
////        PageRequest pageRequest = PageRequest.of(page*size, size);
//        List<Posts> post = boardJpaRepository.findPage(size, page*size);
//        List<BoardResponse> boardResponses = new ArrayList<>();
//        for (Posts p : post) {
//            boardResponses.add(BoardResponse.builder()
//                    .postId(p.getPostId())
//                    .title(p.getTitle())
//                    .content(p.getContent())
//                    .writer(p.getWriter())
//                    .scrap(p.getScrap())
//                    .createdDt(p.getCreatedDt())
//                    .build());
//        }
//        return boardResponses;
//}

    @Transactional
    public Long createBoard(List<MultipartFile> multipartFile, String content, String title, UserInfo userinfo) {
        Posts posts = new Posts();

        Posts post = Posts.builder()
                .userId(userinfo) // 외래 키 값을 가진 객체
                .writer(userinfo.getNickname()) // 외래 키의 닉 네임 값
                .title(title)
                .content(content)
                .scrap(0)
                .build();

        Posts postSave = boardRepository.save(post);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            String date = postSave.getCreatedDt().toString().substring(0, 10);
            if (multipartFile.size() > 0) {

                String folderPath = "/home/devuser/Doori/image/board/" + date;
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

                String savedServerPath = serverFileDir + "board/" + date + "/" + savedName;
                String savedDbPath = dbFileDir + "board/" + date + "/" + savedName;
//                String savedServerPath = serverFileDir + savedName;
//                String savedDbPath = dbFileDir + savedName;


                Image file = Image.builder()
                        .uploadFileName(origName)
                        .storeFileName(savedName)
                        .path(savedDbPath)
                        .post(postSave)
                        .build();

                try {
                    files.transferTo(new File(savedServerPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageRepository.save(file);

            });
        }
        return postSave.getPostId();
    }

    // 게시글과 댓글 열람
    @Transactional
    public BoardResponse getBoard(Long boardId) {
        Optional<Posts> post = boardRepository.findById(boardId);

        if (post.isPresent()) {
            Posts getPost = post.get();

            // 해당 게시글의 댓글 모두 불러오기
            List<Reply> replies = replyRepository.findAllByPost(post);

            // 댓글 정렬하기
            List<ReplyDto.ReplyGroup> groups = sortReply(replies);

            return BoardResponse
                    .builder()
//                    .postId(getPost.getPostId())
//                    .writer(getPost.getWriter())
//                    .title(getPost.getTitle())
//                    .content(getPost.getContent())
//                    .scrap(getPost.getScrap())
//                    .createdDt(getPost.getCreatedDt())
                    .replyGroup(groups)
                    .build();
        } else {
            throw new RuntimeException("찾는 게시글이 없습니다.");
        }
    }

    // 댓글 묶음 받는 메소드
    private List<ReplyDto.ReplyGroup> sortReply(List<Reply> replies) {

        // 해당 게시글의 모든 '부모 댓글 + 자식 댓글' 묶음
        List<ReplyDto.ReplyGroup> groups = new ArrayList<>();

        for (Reply reply : replies) {
            // 자신이 부모인 경우
            if (reply.getParent() == null) {

                // 자식 댓글들 모두 불러오기
                List<Reply> childrenList = reply.getChildren();

                // 모든 자식댓글들의 '닉네임,댓글내용' 담기
                List<ReplyDto.CreateChildrenReplyResponse> allChild = new ArrayList<>();

                for (Reply child : childrenList) {
                    // 자식 댓글의 '닉네임, 댓글내용' 담기
                    ReplyDto.CreateChildrenReplyResponse oneChildGroup = ReplyDto.CreateChildrenReplyResponse.builder()
                            .replyId(child.getReplyId())
                            .nickname(child.getUserId().getNickname())
                            .content(child.getContent())
//                        .children(allChild)
                            .userId(child.getUserId().getUserId())
                            .parentReplyId(child.getParent().getReplyId())
                            .build();

                    allChild.add(oneChildGroup);
                }

                // 부모 댓글과 자식 댓글 묶기
                ReplyDto.ReplyGroup oneGroup = ReplyDto.ReplyGroup.builder()
                        .replyId(reply.getReplyId())
                        .nickname(reply.getUserId().getNickname())
                        .content(reply.getContent())
                        .children(allChild)
                        .userId(reply.getUserId().getUserId())
                        .build();

                groups.add(oneGroup);

            }
        }
        return groups;

    }


    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardRequest boardRequest, UserInfo userinfo) throws Exception {
        var post = boardRepository.findById(boardId).orElseThrow(); // db 안에 저장된 값
        // boardRequest는 board를 수정 할 값들이 들어가있음 "title":"타이틀","body":"바디"

        // board의 작성자 userId랑 요청한 userId랑 같은 경우 수정 가능
        if (userinfo.getUserId().equals(post.getUserId().getUserId())) {
            post.setTitle(boardRequest.getTitle());
            post.setContent(boardRequest.getContent());
            // media 추가해야함
            return BoardResponse
                    .builder()
                    .postId(post.getPostId())
                    .writer(post.getWriter())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .scrap(post.getScrap())
                    .createdDt(post.getCreatedDt())
                    .build();
        }

        throw new RuntimeException("자신의 게시글만 수정할 수 있습니다");

    }

    @Transactional
    public boolean deleteBoard(Long boardId, UserInfo userinfo) {
        UserInfo userInfo = userRepository.findByUserId(userinfo.getUserId()).orElseThrow(() -> new NullPointerException("존재하지 않는 아이디입니다."));

        Posts post = boardRepository.findById(boardId).orElseThrow(); // db 안에 저장된 값
        if (userInfo.getUserId().equals(post.getUserId().getUserId())) {
            boardRepository.deleteById(post.getPostId());
            return true;
        } else {
            throw new RuntimeException("자신의 게시글만 삭제할 수 있습니다");
        }
    }

//    public Long getLastPostId(){
//        return boardRepository.findMaxPostId();
//    }
//    public List<BoardResponse> getBoardList(int size, Long lastPostId) {
////        System.out.println("size: "+size);
////        System.out.println("lastPostId: "+ lastPostId);
//    PageRequest pageRequest = PageRequest.of(0, size); // 페이지를 0으로 고정
////        Pageable pageable = PageRequest.of(page, size);
//        Page<Posts> postsPage = boardRepository.findByPostIdLessThanOrderByPostIdDesc(lastPostId, pageRequest );
////        Page<Posts> postsPage = boardJpaRepository.findPage(lastPostId, pageRequest );
//        List<BoardResponse> boardResponses = new ArrayList<>();
//
//        for (Posts p : postsPage.getContent()) {
//             boardResponses.add(BoardResponse.builder()
//                    .postId(p.getPostId())
//                    .title(p.getTitle())
//                    .content(p.getContent())
//                    .writer(p.getWriter())
//                     .userId(p.getUserId().getUserId())
//                    .likeCount(p.getLikeCount())
//                    .dislikeCount(p.getDislikeCount())
//                    .scrap(p.getScrap())
//                    .createdDt(p.getCreatedDt())
//                    .build());
//        }
////
//        return boardResponses;
//    }


//    public List<BoardResponse> getBoardList(int page) {
//       System.out.println("page: "+page);
//        int size = 15;
////        PageRequest pageRequest = PageRequest.of(page*size, size);
//        List<Posts> post = boardJpaRepository.findPage(size, page*size);
//        List<BoardResponse> boardResponses = new ArrayList<>();
//        for (Posts p : post) {
//            boardResponses.add(BoardResponse.builder()
//                    .postId(p.getPostId())
//                    .title(p.getTitle())
//                    .content(p.getContent())
//                    .writer(p.getWriter())
//                    .likeCount(p.getLikeCount())
//                    .dislikeCount(p.getDislikeCount())
//                    .scrap(p.getScrap())
//                    .createdDt(p.getCreatedDt())
//                    .build());
//        }
//        return boardResponses;
//    }


    // 부모 댓글 생성
    @Transactional
    public ReplyDto.CreateReplyResponse createParentReply(Long boardId, UserInfo userInfo, ReplyDto.CreateReplyRequest request) {
        // 게시글 유효성 확인
        var post = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("게시글이 존재하지 않습니다."));

        // 작성자 유효성 확인
        var user = userRepository.findByUserId(userInfo.getUserId()).orElseThrow(() -> new NullPointerException("작성자가 잘못되었습니다."));

        // 댓글 객체 생성
        Reply newReply = Reply.builder()
                .post(post)
                .userId(user)
                .content(request.getContent())
                .build();

        // 댓글 저장
        replyRepository.save(newReply);


        // 결과 리턴
        return ReplyDto.CreateReplyResponse.builder()
                .replyId(newReply.getId())
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .content(request.getContent())
                .children(newReply.getChildren())
                .build();

    }

    // 자식 댓글 생성
    @Transactional
    public ReplyDto.CreateChildrenReplyResponse createChildReply(Long boardId, Long parentId, UserInfo userInfo, ReplyDto.CreateReplyRequest request) {
        // 게시글 유효성 확인
        Posts post = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("게시글이 존재하지 않습니다."));

        // 작성자 유효성 확인
        UserInfo user = userRepository.findByUserId(userInfo.getUserId()).orElseThrow(() -> new NullPointerException("작성자가 잘못되었습니다."));

        // 부모 댓글 유효성 확인
        Reply parentReply = replyRepository.findById(parentId).orElseThrow(() -> new NullPointerException("부모 댓글이 존재하지 않습니다."));

        Reply newReply = Reply.builder()
                .post(post)
                .userId(user)
                .content(request.getContent())
                .parent(parentReply)
                .build();

        parentReply.addChild(newReply);

        replyRepository.save(newReply);

        return ReplyDto.CreateChildrenReplyResponse.builder()
                .replyId(newReply.getId())
                .nickname(user.getNickname())
                .content(request.getContent())
                .userId(user.getUserId())
                .parentReplyId(parentId)
                .build();

    }


    // 스크랩 --------------------------------------------------------------------
    @Transactional
    public ScrapResponse createScrapBoard(Long postId, UserInfo userinfo) {
        var post = boardRepository.findById(postId).orElseThrow(() -> new NullPointerException("존재하지 않는 게시글입니다."));
        var user = userRepository.findById(userinfo.getUserId()).orElseThrow(() -> new NullPointerException("존재하지 않는 사용자입니다."));

        Optional<UserScraps> scrap = scrapRepository.findByUserIdAndPostId(userinfo, post);
        if (scrap.isPresent()) {
            throw new RuntimeException("이미 스크랩한 게시글 입니다.");
        } else {
            UserScraps userScraps = UserScraps.builder()
                    .userId(user)
                    .postId(post)
                    .build();

            scrapRepository.save(userScraps);

            return ScrapResponse.builder()
                    .postId(userScraps.getPostId().getPostId())
                    .userId(userScraps.getUserId().getUserId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .writer(post.getWriter())
                    .build();
        }
    }

    @Transactional
    public boolean deleteCommentBoard(Long commentId, UserInfo userinfo) {
        // 댓글에 있는 userId와 내 아이디가 같고,
        // 댓글에 있는 replyId가 db의 replyId에 존재하면??

        Reply reply = replyRepository.findById(commentId).orElseThrow(() -> new NullPointerException("존재하지 않는 게시글입니다."));

        if (reply.getUserId().getUserId().equals(userinfo.getUserId())) {
            replyRepository.deleteByReplyId(commentId);
            return true;
        }
        return false;

    }

    @Transactional
    public boolean deleteScrapBoard(Long postId, UserInfo userinfo) {
        var post = boardRepository.findById(postId).orElseThrow(() -> new NullPointerException("존재하지 않는 게시글입니다."));
        Optional<UserScraps> scrap = scrapRepository.findByUserIdAndPostId(userinfo, post);

        if (scrap.isEmpty()) {
            throw new RuntimeException("스크랩 되지 않은 게시글 입니다.");
        }
        scrapRepository.delete(scrap.get());
        return true;
    }

    @Transactional
    public List<ScrapResponse> getScrapBoard(UserInfo userinfo) {

        List<Posts> results = boardRepository.findUserScrapsList(userinfo.getUserId());

        if (!results.isEmpty()) {
            List<ScrapResponse> scrapList = new ArrayList<>();

            for (Posts row : results) {
                scrapList.add(ScrapResponse.builder().postId(row.getPostId()).title(row.getTitle()).content(row.getContent()).writer(row.getWriter()).createdDt(row.getCreatedDt()).build());
            }
            return scrapList;
        } else {
            throw new RuntimeException("아직 스크랩한 게시글이 없습니다.");
        }
    }
//    @Transactional
//    public List<ScrapResponse> getScrapBoard(UserInfo userinfo) {
//
//        List<Object[]> results = scrapRepository.findUserScrapsList(userinfo.getUserId());
//        if (results.isEmpty()) {
//            List<ScrapResponse> scrapList = new ArrayList<>();
//
//            for (Object[] row : results) {
//                Long postId = (Long) row[0];
//                String userId = (String) row[1];
//                String title = (String) row[2];
//                String content = (String) row[3];
//                String writer = (String) row[4];
//
//                ScrapResponse scrap = new ScrapResponse(postId, userId, title, content, writer);
//                scrapList.add(scrap);
//            }
//            return scrapList;
//        } else {
//            throw new RuntimeException("아직 스크랩한 게시글이 없습니다.");
//        }
//    }
}
