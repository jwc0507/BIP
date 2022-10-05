package com.example.week8.service;

import com.example.week8.domain.Member;
import com.example.week8.domain.UserDetailsImpl;
import com.example.week8.domain.enums.Authority;
import com.example.week8.dto.KakaoMemberInfoDto;
import com.example.week8.dto.TokenDto;
import com.example.week8.dto.response.OauthLoginResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.MemberRepository;
import com.example.week8.security.TokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;


@RequiredArgsConstructor
@Service
public class KakaoOauthService {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 전체 response 요청
        String accessToken = getAccessToken(code);

        // 2. accessToken을 이용해 카카오 api호출하여 response 받기(사용자 정보 json받아서 id, email, nickname 빼기)
        KakaoMemberInfoDto kakaoMemberInfo = getkakaoMemberInfo(accessToken);

        // 3. 필요시에 회원가입
        Member kakaoUser = registerKakaoUserIfNeeded(kakaoMemberInfo);

        // 4. 강제 로그인 처리
        forceLogin(kakaoUser, response);

        // 5. 첫 로그인 보너스 지급
        kakaoUser.chkFirstLogin();

        return ResponseDto.success(OauthLoginResponseDto.builder()
                .phoneNumber(kakaoUser.getPhoneNumber())
                .email(kakaoUser.getEmail())
                .build());

    }

    private String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "610f7f90999f8f182434e3cc03ad6415");
        body.add("redirect_uri", "http://localhost:3000/login/kakao");
     //   body.add("redirect_uri", "http://localhost:8080/api/member/kakaologin");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private KakaoMemberInfoDto getkakaoMemberInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoMemberInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoMemberInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String imgUrl = jsonNode.get("kakao_account").get("profile").get("profile_image_url").asText();
        return KakaoMemberInfoDto.builder()
                .id(id)
                .email(email)
                .imageUrl(imgUrl)
                .build();
    }

    private Member registerKakaoUserIfNeeded(KakaoMemberInfoDto kakaoMemberInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoMemberInfo.getId();
        Member kakaoUser = memberRepository.findByKakaoId(kakaoId)
                .orElse(null);
        if (kakaoUser == null) {
            String email = kakaoMemberInfo.getEmail();
            String imageUrl = kakaoMemberInfo.getImageUrl();

            boolean chkExistMember = false;

            // 같은 이메일로 가입된 사람이 있는가?
            Member chkMember = memberRepository.findByEmail(email).orElse(null);
            if (chkMember != null) {
                kakaoUser = chkMember;
                chkExistMember = true;
            }
            if (!chkExistMember) {
                kakaoUser = Member.builder()
                        .kakaoId(kakaoId)
                        .email(email)
                        .profileImageUrl(imageUrl)
                        .point(1000000)
                        .credit(100.0)
                        .firstLogin(true)
                        .pointOnDay(0L)
                        .numOfSelfEvent(0)
                        .numOfDone(0)
                        .password("@")
                        .userRole(Authority.valueOf("ROLE_MEMBER"))
                        .build();
            } else {
                kakaoUser.setKakaoId(kakaoId);
                if (kakaoUser.getEmail() == null)
                    kakaoUser.setEmail(email);
                if (kakaoUser.getProfileImageUrl() == null)
                    kakaoUser.setProfileImageUrl(imageUrl);
            }
            memberRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

    private void forceLogin(Member kakaoUser, HttpServletResponse response) {
        // response header에 token 추가
        TokenDto token = tokenProvider.generateTokenDto(kakaoUser);
        response.addHeader("Authorization", "Bearer " + token.getAccessToken());
        response.addHeader("RefreshToken", token.getRefreshToken());

        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, token.getAccessToken(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}

