package com.example.week8.service;

import com.example.week8.domain.ImageFile;
import com.example.week8.domain.Member;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final AmazonS3Service amazonS3Service;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> imageUpload(MultipartFile file, HttpServletRequest request) {
        // 로그인 확인
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        ResponseDto<?> result = amazonS3Service.uploadFile(file);
        if (!result.isSuccess())
            return result;
        ImageFile imageFile = (ImageFile) result.getData();

        return ResponseDto.success(imageFile.getUrl());
    }

    @Transactional
    public ResponseDto<?> deleteFile(String fileUrl) {
        if (amazonS3Service.removeFile(fileUrl)) {
            log.info("이미지 삭제 실패");
            return ResponseDto.fail("이미지 삭제 실패");
        }
        log.info("이미지 삭제 성공");
        return ResponseDto.success("이미지 삭제 성공");
    }

    private ResponseDto<?> validateCheck(HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }
        return ResponseDto.success(member);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
