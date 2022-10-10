package com.example.week8.service;

import com.example.week8.domain.LoginMember;
import com.example.week8.domain.Member;
import com.example.week8.domain.RefreshToken;
import com.example.week8.domain.UserDetailsImpl;
import com.example.week8.domain.enums.Authority;
import com.example.week8.dto.TokenDto;
import com.example.week8.dto.request.AuthRequestDto;
import com.example.week8.dto.request.DuplicationRequestDto;
import com.example.week8.dto.request.EmailLoginRequestDto;
import com.example.week8.dto.request.LoginRequestDto;
import com.example.week8.dto.response.LoginResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.LoginMemberRepository;
import com.example.week8.repository.MemberRepository;
import com.example.week8.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final LoginMemberRepository loginMemberRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final JavaMailSender javaMailSender;
    private final SseEmitterService sseEmitterService;
//      private final RedisUtil redisUtil;


    // 인증번호 확인
    public boolean chkValidCode(String key, String authCode) {
        // 인증번호 테이블에서 전화번호에 해당하는 인증번호 찾기
        List<LoginMember> getLogin = loginMemberRepository.findByKeyValue(key);
        if (getLogin.isEmpty())
            return false;
        // 여러개의 인증번호가 있을 수 있지만 마지막 값을 찾기 (코드 수정해서 첫 값만 가지게 됨)
        String getAuthCode = getLogin.get(0).getAuthCode();
        // 입력된 값과 DB의 인증번호가 같은지 확인
        if (!getAuthCode.equals(authCode))
            return false;
        // 인증완료되었다면 인증번호 테이블 비워주기
        loginMemberRepository.deleteById(getLogin.get(0).getId());

        // redis를 활용했을 때의 코드.
//        String getKey = redisUtil.getData(key);
//        if (!getKey.equals(authCode))
//            return false;
//        redisUtil.deleteData(key);


        return true;
    }

//    public String getCodeTest(String key) {
//        return redisUtil.getData(key);
//    }

    // 회원가입 or 로그인 (전화번호)
    @Transactional
    public ResponseDto<?> createMember(LoginRequestDto requestDto, HttpServletResponse response) {
        String phoneNumber = requestDto.getPhoneNumber();
        String authCode = requestDto.getAuthCode();

        // 인증번호 확인
        if (!chkValidCode(phoneNumber, authCode))
            return ResponseDto.fail("인증번호가 다릅니다.");

        // 이미 있는 회원이라면 로그인메소드를 실행시킨다.
        Member member = isPresentMember(requestDto.getPhoneNumber());
        if (member == null) {
            // 없는 회원이라면
            member = Member.builder()
                    .phoneNumber(phoneNumber)
                    .point(1000000)
                    .pointOnDay(0L)
                    .credit(100.0)
                    .firstLogin(true)
                    .password("@")
                    .numOfDone(0)
                    .numOfSelfEvent(0)
                    .userRole(Authority.valueOf("ROLE_MEMBER"))
                    .build();
            memberRepository.save(member);
        }
        // 로그인 시키기
        return login(member, response);
    }

    // 이메일 로그인
    public ResponseDto<?> emailLogin(EmailLoginRequestDto requestDto, HttpServletResponse response) {
        String email = requestDto.getEmail();
        String authCode = requestDto.getAuthCode();

        Member member = isPresentEmail(email);
        if (member == null)
            return ResponseDto.fail("등록되지 않은 이메일 입니다.");

        // 인증번호 확인
        if (!chkValidCode(email, authCode))
            return ResponseDto.fail("인증번호가 다릅니다.");

        // 로그인 시키기 (당근에서는 바로 전화번호 변경하는 창이 실행됨)
        return login(member, response);
    }


    // 로그인하기
    public ResponseDto<?> login(Member member, HttpServletResponse response) {
        TokenDto tokenDto = tokenProvider.generateTokenDto(member);

        tokenToHeaders(tokenDto, response);

        member.chkFirstLogin();

        return ResponseDto.success(LoginResponseDto.builder().nickname(member.getNickname()).build());
    }

    // 로그아웃
    @Transactional
    public ResponseDto<?> logout(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken")))
            return ResponseDto.fail("토큰 값이 올바르지 않습니다.");

        // 맴버객체 찾아오기
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member)
            return ResponseDto.fail("사용자를 찾을 수 없습니다.");
        if (tokenProvider.deleteRefreshToken(member))
            return ResponseDto.fail("존재하지 않는 Token 입니다.");

        tokenProvider.deleteRefreshToken(member);

        sseEmitterService.deleteAllEmitterStartWithId(member.getId().toString());

        return ResponseDto.success("로그아웃 성공");
    }

    // 인증번호 생성
    @Transactional
    public ResponseDto<?> sendAuthCode(AuthRequestDto requestDto) {
        String code = generateCode();

        List<LoginMember> getLogin = loginMemberRepository.findByKeyValue(requestDto.getValue());
        for (LoginMember getLoginMember : getLogin) {
            loginMemberRepository.deleteById(getLoginMember.getId());
        }
        LoginMember loginMember = LoginMember.builder()
                .authCode(code)
                .keyValue(requestDto.getValue())
                .build();
        loginMemberRepository.save(loginMember);

        // redis활용시의 코드
//        redisUtil.setDataExpire(requestDto.getValue(), code, 300);

        return ResponseDto.success(code);
    }

    // EMAIL 인증번호 발급
    @Transactional
    public ResponseDto<?> sendEmailCode(AuthRequestDto requestDto) {
        // 등록된 이메일인지 확인
        String email = requestDto.getValue();
        Optional<Member> getMember = memberRepository.findByEmail(email);
        if (getMember.isEmpty())
            return ResponseDto.fail("등록되지 않은 이메일 입니다.");

        ResponseDto<?> getAuthCode = sendAuthCode(requestDto);
        if (!getAuthCode.isSuccess())
            return ResponseDto.fail("코드생성 실패");

        String subject = "[BIP] 이메일 로그인 인증코드입니다";
        String text = "인증번호 [" + getAuthCode.getData() + "] 을 입력해주세요.";

        // simpleMailMessage를 사용하면 텍스트만 보내고 MimeMessage를 사용시 멀티파트로 보냄 (파일전송 가능)
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mailHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            mailHelper.setTo(requestDto.getValue());
            mailHelper.setSubject(subject);
            mailHelper.setText(text);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            return ResponseDto.fail("잘못된 이메일 주소입니다.");
        }
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setTo(requestDto.getValue());
//        simpleMailMessage.setSubject(subject);
//        simpleMailMessage.setText(text);
//        javaMailSender.send(simpleMailMessage);

        return ResponseDto.success("인증번호 전송완료");
    }

    // 인증번호 생성
    private String generateCode() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append((int) Math.floor(Math.random() * 10));
        }
        return stringBuilder.toString();
    }

    // 카카오 로그인용 전화번호 체크
    public ResponseDto<?> getMemberByPhoneNumber(DuplicationRequestDto requestDto) {
        String regExp = "(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$";
        if (!Pattern.matches(regExp, requestDto.getValue()))
            return ResponseDto.fail("전화번호 형식을 지켜주세요.");

        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(requestDto.getValue());
        if (optionalMember.isPresent())
            return ResponseDto.success(optionalMember.get());

        return ResponseDto.success(null);
    }

    // 이미 있는 회원인지 체크 (프론트에서 뷰 넘길때 사용하기 위함)
    public ResponseDto<?> checkPhoneNumber(DuplicationRequestDto requestDto) {
        String regExp = "(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$";
        if (!Pattern.matches(regExp, requestDto.getValue()))
            return ResponseDto.fail("전화번호 형식을 지켜주세요.");

        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(requestDto.getValue());
        if (optionalMember.isPresent())
            return ResponseDto.success(false);

        return ResponseDto.success(true);
    }


    // 닉네임 중복 검사
    public ResponseDto<?> checkNickname(DuplicationRequestDto requestDto) {
        String regExp = "^[가-힣a-zA-Z0-9]{2,10}$";
        if (!Pattern.matches(regExp, requestDto.getValue()))
            return ResponseDto.fail("2~10자리 한글,대소문자,숫자만 입력해주세요.");

        Optional<Member> optionalMember = memberRepository.findByNickname(requestDto.getValue());
        if (optionalMember.isPresent())
            return ResponseDto.fail("중복된 닉네임 입니다.");

        return ResponseDto.success("사용 가능한 닉네임 입니다.");
    }

    // 이메일 중복 검사
    public ResponseDto<?> checkEmail(DuplicationRequestDto requestDto) {
        String regExp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        if (!Pattern.matches(regExp, requestDto.getValue()))
            return ResponseDto.fail("이메일 양식을 지켜주세요.");

        Optional<Member> optionalMember = memberRepository.findByEmail(requestDto.getValue());
        if (optionalMember.isPresent())
            return ResponseDto.fail("사용중인 이메일 입니다.");

        return ResponseDto.success("사용 가능한 이메일 입니다.");
    }

    // 전화번호로 멤버 검색
    @Transactional(readOnly = true)
    public Member isPresentMember(String phoneNumber) {
        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(phoneNumber);
        return optionalMember.orElse(null);
    }

    // 이메일로 멤버 검색
    @Transactional(readOnly = true)
    public Member isPresentEmail(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        return optionalMember.orElse(null);
    }

    // 토큰 재발급
    @Transactional
    public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return ResponseDto.fail("토큰이 유효하지 않습니다.");
        }
        String memberId = tokenProvider.getMemberFromExpiredAccessToken(request);
        if (null == memberId) {
            return ResponseDto.fail("토큰의 값이 유효하지 않습니다.");
        }
        Member member = memberRepository.findById(Long.parseLong(memberId)).orElse(null);

        RefreshToken refreshToken = tokenProvider.isPresentRefreshToken(member);

        if (!refreshToken.getKeyValue().equals(request.getHeader("RefreshToken"))) {
            log.info("refreshToken : "+refreshToken.getKeyValue());
            log.info("header rft : "+request.getHeader("RefreshToken"));
            return ResponseDto.fail("토큰이 일치하지 않습니다.");
        }
        assert member != null;
        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        refreshToken.updateValue(tokenDto.getRefreshToken());
        tokenToHeaders(tokenDto, response);
        return ResponseDto.success("재발급 완료");
    }

    // 로그인 체크
    public ResponseDto<?> chkLogin(UserDetailsImpl userDetail) {
        if(userDetail == null)
            return ResponseDto.success(false);
        return ResponseDto.success(true);
    }


    // 헤더에 토큰담기
    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("RefreshToken", tokenDto.getRefreshToken());
    }


}
