package com.example.week8.controller;

import com.example.week8.dto.response.ResponseDto;
import com.example.week8.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class fileController {

    private final FileService fileService;

    @RequestMapping (value = "/api/image", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseDto<?> imageUpload(@RequestPart("file") MultipartFile file, HttpServletRequest request) throws IOException {
        return fileService.imageUpload(file, request);
    }
}
