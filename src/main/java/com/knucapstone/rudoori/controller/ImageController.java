//package com.knucapstone.rudoori.controller;
//
//import com.knucapstone.rudoori.repository.ImageRepository;
//import com.knucapstone.rudoori.service.ImageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//@RequestMapping("/image")
//@RequiredArgsConstructor
//public class ImageController {
//
//    private final ImageService imageService;
//
//    @Value("${file.serverDir}")
//    private String fileDir;
//
//    @GetMapping("/test")
//    public String test() {
//        String imageName = "apple.jpeg";
//        return fileDir+ imageName;
//    }
//
//    @PostMapping("/upload")
//    public String uploadFile(@RequestParam List<MultipartFile> files) throws IOException {
//
//        for (MultipartFile multipartFile : files) {
//            imageService.saveFile(multipartFile);
//        }
//
//        return "ok";
//    }
//
//    @GetMapping("/{postId}")
//    public List<String> showImage(@PathVariable Long postId)  {
//        return imageService.getImage(postId);
//    }
//
////    @PostMapping("/file")
////    public ResponseEntity uploadFile(@RequestPart List<MultipartFile> multipartFile) {
////
////        return new ResponseEntity<>(imageService.uploadFile(multipartFile), HttpStatus.OK);
////    }
//
//}