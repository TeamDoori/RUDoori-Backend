//package com.knucapstone.rudoori.service;
//
//import com.knucapstone.rudoori.model.entity.Image;
//import com.knucapstone.rudoori.repository.BoardRepository;
//import com.knucapstone.rudoori.repository.ImageRepository;
//import com.knucapstone.rudoori.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//@RequiredArgsConstructor
//@Service
//public class ImageService {
//
//    private final ImageRepository imageRepository;
//    private final BoardRepository boardRepository;
//

//    @Value("${file.serverDir}")
//    private String serverFileDir;
//
//    public Long saveFile(MultipartFile multipartFile) throws IOException {
//        String origName = multipartFile.getOriginalFilename();
//        String uuid = UUID.randomUUID().toString();
//        String extension = origName.substring(origName.lastIndexOf("."));
//        String savedName = uuid + extension;
//        String savedPath = serverFileDir + savedName;
//
//
//        String savedServerPath = serverFileDir + "board/" + savedName;
//        String savedDbPath = dbFileDir + "board/" + savedName;
//
//        Image file = Image.builder()
//                .uploadFileName(origName)
//                .storeFileName(savedName)
//                .path(savedDbPath)
//                .userId()
//                .build();
//
//
//        multipartFile.transferTo(new File(savedPath));

//
//        Image savedFile = imageRepository.save(file);
//
//        return savedFile.getId();
//    }
//
//    public List<String> getImage(Long postId) {
//        List<Image> allByPostId = imageRepository.findAllByPost(boardRepository.findById(postId).get());
//        List<String> imageList = new ArrayList<>();
//        for(Image image: allByPostId){
//            imageList.add(image.getPath());
//            System.out.println(image.getPath());
//
//        }
////        List<Image> allById = imageRepository.findAllById(Collections.singleton(id));
////        String storedImageUrl = null;
////        for (Image image : allById) {
////            storedImageUrl = image.getPath();
////        }
//        return imageList;
//    }
//}