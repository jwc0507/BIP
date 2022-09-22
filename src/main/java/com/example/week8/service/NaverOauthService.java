package com.example.week8.service;

import com.example.week8.domain.Member;
import com.example.week8.domain.UserDetailsImpl;
import com.example.week8.domain.enums.Authority;
import com.example.week8.dto.NaverMemberInfoDto;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Service
public class NaverOauthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public ResponseDto<?> naverlogin(String code, String state, HttpServletResponse response) throws JsonProcessingException {

        // 1. 받은 code와 state로 accesstoken 받기
        String accessToken = getAccessToken(code, state);

        // 2. accesstoken으로 유저정보받기
        NaverMemberInfoDto memberInfoDto = getNaverMemberInfo(accessToken);

        // 3. 필요시에 회원가입
        Member naverMember = registerNaverMemberIfNeeded(memberInfoDto);

        // 4. 강제 로그인 처리
        forceLogin(naverMember, response);

        return ResponseDto.success(OauthLoginResponseDto.builder()
                .nickname(naverMember.getNickname())
                .phoneNumber(naverMember.getPhoneNumber())
                .email(naverMember.getEmail())
                .build());
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
        String nickname = jsonNode.get("response").get("name").asText();
        String email = jsonNode.get("response").get("email").asText();
        String imgUrl = jsonNode.get("response").get("profile_image").asText();
        String mobile = jsonNode.get("response").get("mobile").asText();

        String phoneNumber = mobile.replaceAll("-", "");
        return NaverMemberInfoDto.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .imageUrl(imgUrl)
                .phoneNumber(phoneNumber)
                .build();
    }

    private Member registerNaverMemberIfNeeded(NaverMemberInfoDto memberInfoDto) {
        String naverId = memberInfoDto.getId();
        Member naverMember = memberRepository.findByNaverId(naverId)
                .orElse(null);
        if (naverMember == null) {
            String nickname = memberInfoDto.getNickname();
            String email = memberInfoDto.getEmail();
            String imgUrl = memberInfoDto.getImageUrl();
            String phoneNumber = memberInfoDto.getPhoneNumber();

            boolean chkExistMember = false;

            // 같은 폰번호로 가입된 사람이 있는가?
            Member chkMember = memberRepository.findByPhoneNumber(phoneNumber).orElse(null);
            if (chkMember != null) {
                naverMember = chkMember;
                chkExistMember = true;
            }
            // 같은 이메일로 가입된 사람이 있는가?
            chkMember = memberRepository.findByEmail(email).orElse(null);
            if (chkMember != null) {
                naverMember = chkMember;
                chkExistMember = true;
            }
            if (!chkExistMember) {
                naverMember = Member.builder()
                        .naverId(naverId)
                        .email(email)
                        .profileImageUrl(imgUrl)
                        .phoneNumber(phoneNumber)
                        .point(1000)
                        .credit(100.0)
                        .numOfDone(0)
                        .password("@")
                        .userRole(Authority.valueOf("ROLE_MEMBER"))
                        .build();
            } else {
                naverMember.setNaverId(naverId);
                naverMember.setEmail(email);
                naverMember.setPhoneNumber(phoneNumber);
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
