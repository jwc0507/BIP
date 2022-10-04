//package com.example.week8.controller;
//
//import com.example.week8.dto.response.ResponseDto;
//import com.example.week8.utils.firebase.FirebaseService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//
//@RestController
//@RequiredArgsConstructor
//public class FCMController {
//    private final FirebaseService firebaseService;
//
//    @RequestMapping (value = "/api/sendAlert/{deviceToken}", method = RequestMethod.POST)
//    public ResponseDto<?> sendAlert(@PathVariable String deviceToken) throws IOException {
//        return firebaseService.sendMessageTo(deviceToken, "제목", "내용");
//    }
//
//}
