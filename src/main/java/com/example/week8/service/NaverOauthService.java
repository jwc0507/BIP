package com.example.week8.service;

import com.example.week8.domain.Member;
import com.example.week8.domain.UserDetailsImpl;
import com.example.week8.domain.enums.Authority;
import com.example.week8.dto.NaverMemberInfoDto;
import com.example.week8.dto.SignupInfoDto;
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
@Transactional
public class NaverOauthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> naverlogin(String code, String state, HttpServletResponse response) throws JsonProcessingException {

        // 1. 받은 code와 state로 accesstoken 받기
        String accessToken = getAccessToken(code, state);

        // 2. accesstoken으로 유저정보받기
        NaverMemberInfoDto memberInfoDto = getNaverMemberInfo(accessToken);

        // 3. 필요시에 회원가입
        Member naverMember = registerNaverMemberIfNeeded(memberInfoDto);

        // 4. 강제 로그인 처리
        forceLogin(naverMember, response);

        // 5. 로그인 보너스 주기
        naverMember.chkFirstLogin();

        return ResponseDto.success(OauthLoginResponseDto.builder()
                .nickname(naverMember.getNickname())
                .phoneNumber(naverMember.getPhoneNumber())
                .email(naverMember.getEmail())
                .build());
    }

    // 로그인 연동 해제
    @Transactional
    public ResponseDto<?> naverLogout(String code, String state) throws JsonProcessingException{
        // 1. 받은 code와 state로 accesstoken 받기
        String accessToken = getAccessToken(code, state);
        // 2. 로그인연동 해제
        return ResponseDto.success(doLogout(accessToken));
    }

    // 연동 해제 요청 실행
    private String doLogout(String accessToken) throws JsonProcessingException {
        HttpHeaders logoutHeaders = new HttpHeaders();
        logoutHeaders.add("Content-type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> logoutRequestParam = new LinkedMultiValueMap<>();
        logoutRequestParam.add("grant_type", "delete");
        logoutRequestParam.add("client_id", "z6KYvnNk_EXQGnwgQo3u");
        logoutRequestParam.add("client_secret", "EyjWue7YLp");
        logoutRequestParam.add("access_token", accessToken);
        logoutRequestParam.add("service_provider", "NAVER");    // api랑 다름 이거 안붙이면 invaild_provider 에러발생함.

        HttpEntity<MultiValueMap<String, String>> logoutRequest = new HttpEntity<>(logoutRequestParam, logoutHeaders);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> logoutResponse = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                logoutRequest,
                String.class
        );
        String responseBody = logoutResponse.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("result").asText();
    }


    private String getAccessToken(String code, String state) throws JsonProcessingException {

        HttpHeaders accessTokenHeaders = new HttpHeaders();
        accessTokenHeaders.add("Content-type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> accessTokenParams = new LinkedMultiValueMap<>();
        accessTokenParams.add("grant_type", "authorization_code");
        accessTokenParams.add("client_id", "z6KYvnNk_EXQGnwgQo3u");
        accessTokenParams.add("client_secret", "EyjWue7YLp");
        accessTokenParams.add("state", state);
        accessTokenParams.add("code", code);    // 응답으로 받은 코드

        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = new HttpEntity<>(accessTokenParams, accessTokenHeaders);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                accessTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = accessTokenResponse.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private NaverMemberInfoDto getNaverMemberInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverMemberInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                naverMemberInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String id = jsonNode.get("response").get("id").asText();
        String email = jsonNode.get("response").get("email").asText();
        String imgUrl = null;
        try {
            imgUrl = jsonNode.get("response").get("profile_image").asText();
        }
        catch (Exception ignored){
        }
        return NaverMemberInfoDto.builder()
                .id(id)
                .email(email)
                .imageUrl(imgUrl)
                .build();
    }

    private Member registerNaverMemberIfNeeded(NaverMemberInfoDto memberInfoDto) {
        String naverId = memberInfoDto.getId();
        Member naverMember = memberRepository.findByNaverId(naverId)
                .orElse(null);
        if (naverMember == null) {
            String email = memberInfoDto.getEmail();
            String imgUrl = memberInfoDto.getImageUrl();

            boolean chkExistMember = false;

            // 같은 이메일로 가입된 사람이 있는가?
            Member chkMember = memberRepository.findByEmail(email).orElse(null);
            if (chkMember != null) {
                naverMember = chkMember;
                chkExistMember = true;
            }
            if (!chkExistMember) {
                naverMember = new Member(SignupInfoDto.builder()
                        .naverId(naverId)
                        .imgUrl(imgUrl)
                        .email(email)
                        .role(Authority.ROLE_MEMBER)
                        .build());
            } else {
                naverMember.setNaverId(naverId);
                if (naverMember.getEmail() == null)
                    naverMember.setEmail(email);
                naverMember.setPhoneNumber(null);
                if (naverMember.getProfileImageUrl() == null)
                    naverMember.setProfileImageUrl(imgUrl);
            }
            memberRepository.save(naverMember);
        }
        return naverMember;
    }

    private void forceLogin(Member naverMember, HttpServletResponse response) {
        // response header에 token 추가
        TokenDto token = tokenProvider.generateTokenDto(naverMember);
        response.addHeader("Authorization", "Bearer " + token.getAccessToken());
        response.addHeader("RefreshToken", token.getRefreshToken());

        UserDetails userDetails = new UserDetailsImpl(naverMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, token.getAccessToken(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}