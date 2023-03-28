package com.example.week8.service;

import com.example.week8.domain.*;
import com.example.week8.domain.chat.ChatRoom;
import com.example.week8.domain.enums.AlertType;
import com.example.week8.domain.enums.Attendance;
import com.example.week8.domain.enums.BeforeTime;
import com.example.week8.domain.enums.EventStatus;
import com.example.week8.dto.request.*;
import com.example.week8.dto.response.EventListDto;
import com.example.week8.dto.response.EventResponseDto;
import com.example.week8.dto.response.MemberResponseDto;
import com.example.week8.dto.response.ResponseDto;
import com.example.week8.repository.*;
import com.example.week8.dto.response.*;
import com.example.week8.security.TokenProvider;
import com.example.week8.utils.time.Time;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventService {

    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;
    private final CheckinMemberRepository checkinMemberRepository;
    private final TokenProvider tokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final EventScheduleRepository eventScheduleRepository;
    private final FriendRepository friendRepository;
    private final WeatherService weatherService;
    private final SseEmitterService sseEmitterService;
    private final int MAG_DONE_CREDIT = 1;  // 약속완료 신용도 증감 배율 (1이 기본)


    // 날짜 체크 api
    public ResponseDto<?> chkDateTime(DuplicationRequestDto requestDto) {
        if(Time.diffTime(stringToLocalDateTime(requestDto.getValue()), LocalDateTime.now()))
            return ResponseDto.fail(false);
        return ResponseDto.fail(true);
    }

    // 채팅방 개설
    @Transactional
    public void createChatRoom(Event event) {
        ChatRoom chatRoom = ChatRoom.builder()
                .id(event.getId())
                .name(event.getTitle())
                .event(event)
                .build();
        chatRoomRepository.save(chatRoom);
    }


    /**
     * 약속 생성
     */
    @Transactional
    public ResponseDto<?> createEvent(EventRequestDto eventRequestDto,
                                      HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 엔티티 조회
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }

        if(Time.diffTime(stringToLocalDateTime(eventRequestDto.getEventDateTime()), LocalDateTime.now()))
            return ResponseDto.success("약속 시간을 미래로 설정해주세요.");


        // 약속 생성
        Event event = Event.builder()
                .title(eventRequestDto.getTitle())
                .master(member)
                .eventStatus(EventStatus.ONGOING)
                .eventDateTime(stringToLocalDateTime(eventRequestDto.getEventDateTime()))
                .place(eventRequestDto.getPlace())
                .coordinate(eventRequestDto.getCoordinate())
                .content(eventRequestDto.getContent())
                .point(eventRequestDto.getPoint())
                .build();
        eventRepository.save(event);


        // 약속 멤버 생성
        EventMember eventMember = new EventMember(member, event);  // 생성 시에는 약속을 생성한 member만 존재
        eventMemberRepository.save(eventMember);

        // MemberResponseDto에 Member 담기
        List<MemberResponseDto> list = new ArrayList<>();
        MemberResponseDto memberResponseDto = convertToDto(member, null);
        list.add(memberResponseDto);

        eventRepository.flush();
        eventMemberRepository.flush();


        // 약속 스케쥴 생성 - 주, 일, 시, 분
        createEventSchedule(event);
        createChkin(event.getId());
        WeatherInfo weatherInfo = weatherService.saveLocalWeatherInfo(event, event.getCoordinate());
        event.setWeather(weatherInfo);

        return ResponseDto.success(
                EventResponseDto.builder()
                        .id(event.getId())
                        .memberList(list)
                        .eventStatus(event.getEventStatus())
                        .title(event.getTitle())
                        .eventDateTime(Time.serializeEventDate(event.getEventDateTime()))
                        .place(event.getPlace())
                        .coordinate(event.getCoordinate())
                        .createdAt(Time.serializePostDate(event.getCreatedAt()))
                        .lastTime(Time.convertLocaldatetimeToTime(event.getEventDateTime()))
                        .weatherResponseDto(weatherService.getWeatherInfo(event))
                        .content(event.getContent())
                        .point(event.getPoint())
                        .build()
        );
    }

    // 약속 스케쥴 생성
    public void createEventSchedule(Event event) {
        // 알림 시간이 이미 지난 시점이라면 해당 eventSchedule은 생성하지 않음

        if (event.getEventDateTime().minusDays(1).isAfter(LocalDateTime.now())) {
            EventSchedule eventScheduleDay = new EventSchedule(event);
            eventScheduleDay.setBeforeTime(BeforeTime.DAY);
            eventScheduleDay.setTargetTime(event.getEventDateTime().minusDays(1));
            eventScheduleRepository.save(eventScheduleDay);
        }

        if (event.getEventDateTime().minusHours(1).isAfter(LocalDateTime.now())) {
            EventSchedule eventScheduleHour = new EventSchedule(event);
            eventScheduleHour.setBeforeTime(BeforeTime.HOUR);
            eventScheduleHour.setTargetTime(event.getEventDateTime().minusHours(1));
            eventScheduleRepository.save(eventScheduleHour);
        }

        if (event.getEventDateTime().minusMinutes(1).isAfter(LocalDateTime.now())) {
            EventSchedule eventScheduleMinute = new EventSchedule(event);
            eventScheduleMinute.setBeforeTime(BeforeTime.MINUTE);
            eventScheduleMinute.setTargetTime(event.getEventDateTime().minusMinutes(10));
            eventScheduleRepository.save(eventScheduleMinute);
        }
    }

    // 기본방장 체크인생성
    public void createChkin(Long eventId) {
        // 체크인멤버 생성 - 초대하는 사람 것
        Event event = eventRepository.findById(eventId).orElse(null);  // 이벤트 찾기
        EventMember eventMember = eventMemberRepository.findAllByEventId(event.getId()).get(0);
        Member member = memberRepository.findById(eventMember.getMember().getId()).orElse(null);    // 멤버찾기 (방장)

        // 채팅방도 개설해준다.
        createChatRoom(event);

        if (isPresentCheckinMember(event.getId(), member.getId() ) == null) {
            CheckinMember checkinMember = new CheckinMember(event, isPresentMember(member.getId()));
            checkinMemberRepository.save(checkinMember);
        } else {
            CheckinMember checkinMember = isPresentCheckinMember(event.getId(), member.getId());
            checkinMemberRepository.save(checkinMember);
        }
    }

    /**
     * 약속 수정
     */
    public ResponseDto<?> updateEvent(Long eventId,
                                      @RequestBody EventRequestDto eventRequestDto,
                                      HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 엔티티 조회
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }
        // 약속 호출
        Event event = isPresentEvent(eventId);
        if (null == event) {
            return ResponseDto.fail("ID_NOT_FOUND");
        }
        // 약속 수정은 방장만 할 수 있게
        if (!isMaster(event, member))
            return ResponseDto.fail("방장이 아닙니다.");

        if(!event.getCoordinate().equals(eventRequestDto.getCoordinate())) {
            weatherService.updateLocalWeatherInfo(event, eventRequestDto.getCoordinate());
        }

        // eventDateTime에 변경이 있는지 확인
        if (!event.getEventDateTime().isEqual(stringToLocalDateTime(eventRequestDto.getEventDateTime()))) {
            event.updateEvent(eventRequestDto);  // 약속 수정
            eventScheduleRepository.deleteAllByEventId(eventId);  // 기존 약속스케쥴 삭제
            createEventSchedule(event);  // 새로운 약속스케쥴 생성
        }
        event.updateEvent(eventRequestDto);  // 약속 수정

        // MemberResponseDto에 Member 담기
        List<MemberResponseDto> list = new ArrayList<>();
        MemberResponseDto memberResponseDto = convertToDto(member, null);
        list.add(memberResponseDto);

        return ResponseDto.success(
                EventResponseDto.builder()
                        .id(event.getId())
                        .memberList(list)
                        .eventStatus(event.getEventStatus())
                        .title(event.getTitle())
                        .eventDateTime(Time.serializeEventDate(event.getEventDateTime()))
                        .place(event.getPlace())
                        .coordinate(event.getCoordinate())
                        .createdAt(Time.serializePostDate(event.getCreatedAt()))
                        .lastTime(Time.convertLocaldatetimeToTime(event.getEventDateTime()))
//                        .weatherResponseDto((WeatherResponseDto)weatherService.getLocalWeather(event.getCoordinate()).getData())
                        .weatherResponseDto(weatherService.getWeatherInfo(event))
                        .content(event.getContent())
                        .point(event.getPoint())
                        .build()
        );
    }

    /**
     * 약속 목록 조회
     */
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllEvent(String unit,
                                      String inputDate,
                                      HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 엔티티 조회
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }
        LocalDateTime queryDate = stringToLocalDateTime(inputDate);  // 사용자가 조회 요청한 날짜

        List<EventMember> eventMemberList = eventMemberRepository.findAllByMemberId(member.getId());
        List<EventListDto> tempList = new ArrayList<>();
        List<MonthEventListDto> monthEventListDtoList = new ArrayList<>();
        int chkDay = 0;
        int dateEventCounter = 0;
        LocalDateTime lastEventDate = queryDate;

        for (EventMember eventMember : eventMemberList) {

            Event event = isPresentEvent(eventMember.getEvent().getId());
            LocalDateTime eventDateTime = event.getEventDateTime();

            switch (unit) {
                case "day":
                    if (eventDateTime.getYear() == queryDate.getYear()
                            && eventDateTime.getDayOfYear() == queryDate.getDayOfYear()) {
                        tempList.add(convertToDto(event));
                    }
                    break;
                case "week":
                    if (eventDateTime.getYear() == queryDate.getYear()
                            && queryDate.getDayOfYear() <= eventDateTime.getDayOfYear()
                            && eventDateTime.getDayOfYear() <= queryDate.plusDays(6).getDayOfYear()) {

                        tempList.add(convertToDto(event));
                    }
                    break;
                case "month":
                    if (eventDateTime.getYear() == queryDate.getYear()
                            && queryDate.getMonth() == eventDateTime.getMonth()) {
                        if (chkDay == 0)
                            chkDay = eventDateTime.getDayOfMonth();
                        if (chkDay != eventDateTime.getDayOfMonth()) {
                            monthEventListDtoList.add(MonthEventListDto.builder()
                                    .eventDateTime(Time.serializeEventDate(lastEventDate))
                                    .numberOfEventInToday(dateEventCounter)
                                    .build());
                            chkDay = eventDateTime.getDayOfMonth();
                            dateEventCounter = 0;
                        }
                        dateEventCounter++;
                        lastEventDate = eventDateTime;
                    }
                    break;
            }
        }
        if (unit.equals("month")) {
            monthEventListDtoList.add(MonthEventListDto.builder()
                    .eventDateTime(Time.serializeEventDate(lastEventDate))
                    .numberOfEventInToday(dateEventCounter)
                    .build());
            return ResponseDto.success(monthEventListDtoList);
        }
        return ResponseDto.success(tempList);
    }


    /**
     * 약속 단건 조회
     */
    @Transactional(readOnly = true)
    public ResponseDto<?> getEvent(Long eventId, HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }
        Event event = isPresentEvent(eventId);
        if (null == event) {
            return ResponseDto.fail("약속이 존재하지 않습니다.");
        }
        if (eventMemberRepository.findByEventIdAndMemberId(eventId, member.getId()).isEmpty())
            return ResponseDto.fail("약속 참여자가 아닙니다.");


        // MemberResponseDto에 Member 담기
        List<EventMember> findEventMemberList = eventMemberRepository.findAllByEventId(eventId);
        List<MemberResponseDto> tempList = new ArrayList<>();
        for (EventMember eventMember : findEventMemberList) {
            Long memberId = eventMember.getMember().getId();
            MemberResponseDto memberResponseDto = convertToDto(member,isPresentMember(memberId));
            tempList.add(memberResponseDto);
        }

        return ResponseDto.success(
                EventResponseDto.builder()
                        .id(event.getId())
                        .memberList(tempList)
                        .eventStatus(event.getEventStatus())
                        .title(event.getTitle())
                        .eventDateTime(Time.serializeEventDate(event.getEventDateTime()))
                        .place(event.getPlace())
                        .coordinate(event.getCoordinate())
                        .createdAt(Time.serializePostDate(event.getCreatedAt()))
                        .lastTime(Time.convertLocaldatetimeToTime(event.getEventDateTime()))
                        .weatherResponseDto(weatherService.getWeatherInfo(event))
                        .content(event.getContent())
                        .point(event.getPoint())
                        .build()
        );
    }

    /**
     * 약속 삭제
     */
    public ResponseDto<?> deleteEvent(Long eventId, HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 엔티티 조회
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }
        // 약속 호출
        Event event = isPresentEvent(eventId);
        if (null == event) {
            return ResponseDto.fail("ID_NOT_FOUND");
        }
        // 약속 수정은 방장만 할 수 있게
        if (!isMaster(event, member))
            return ResponseDto.fail("방장이 아닙니다.");


        // 약속 삭제, 약속과 연관된 약속멤버도 함께 삭제됨
        eventRepository.deleteById(eventId);
        return ResponseDto.success("약속이 삭제되었습니다.");
    }

    /**
     * 약속 초대(약속멤버 추가)
     */
    public ResponseDto<?> inviteMember(Long eventId,
                                       InviteMemberDto inviteMemberDto,
                                       HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        if (eventMemberRepository.findByEventIdAndMemberId(eventId, member.getId()).isEmpty())
            return ResponseDto.fail("약속 참여자가 아닙니다.");

        // 닉네임에 해당하는(초대할) 멤버 호출
        Member guest = isPresentMemberByNickname(inviteMemberDto.getNickname()); //친구가 설정한 닉네임.
        if (null == guest) {
            return ResponseDto.fail("MEMBER_NOT_FOUND");
        }
        if (eventMemberRepository.findByEventIdAndMemberId(eventId, guest.getId()).isPresent())
            return ResponseDto.fail("이미 참여하고 있는 회원입니다.");

        // 약속 호출
        Event event = isPresentEvent(eventId);

        // 약속 멤버 생성
        EventMember tempEventMember = new EventMember(guest, event);
        eventMemberRepository.save(tempEventMember);

        // 체크인멤버 생성 - 초대받는 사람 것이 생기고 있음
        CheckinMember checkinMemberGuest = new CheckinMember(event, isPresentMember(guest.getId()));
        checkinMemberRepository.save(checkinMemberGuest);

        // MemberResponseDto에 Member 담기
        List<EventMember> findEventMemberList = eventMemberRepository.findAllByEventId(eventId);
        List<MemberResponseDto> tempList = new ArrayList<>();
        for (EventMember eventMember : findEventMemberList) {
            MemberResponseDto memberResponseDto = convertToDto(member,eventMember.getMember());

            // 체크인멤버 호출
            CheckinMember tempCheckinMember = isPresentCheckinMember(eventId, eventMember.getMember().getId());
            memberResponseDto.setAttendance(tempCheckinMember.getAttendance());
            tempList.add(memberResponseDto);
        }

        // 채팅방 초대 알림
        sseEmitterService.pubEventInvite(guest.getId(), event);

        return ResponseDto.success(
                EventResponseDto.builder()
                        .id(event.getId())
                        .memberList(tempList)
                        .eventStatus(event.getEventStatus())
                        .title(event.getTitle())
                        .eventDateTime(Time.serializeEventDate(event.getEventDateTime()))
                        .place(event.getPlace())
                        .coordinate(event.getCoordinate())
                        .createdAt(Time.serializePostDate(event.getCreatedAt()))
                        .lastTime(Time.convertLocaldatetimeToTime(event.getEventDateTime()))
                        .weatherResponseDto(weatherService.getWeatherInfo(event))
                        .content(event.getContent())
                        .point(event.getPoint())
                        .build()
        );
    }

    /**
     * 약속 탈퇴
     */
    @Transactional
    public ResponseDto<?> exitEvent(Long eventId, HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 멤버 호출
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }
        // 약속 호출
        Event event = isPresentEvent(eventId);

        // 약속 멤버 호출
        EventMember eventMember = isPresentEventMember(event, member);
        if (eventMember == null) {
            log.info("약속에 참여하지 않은 회원입니다, line : 294");
            return ResponseDto.fail("약속에 참여하지 않은 회원입니다.");
        }

        // 체크인에서도 지우기
        List<CheckinMember> checkinMemberList = checkinMemberRepository.findByEventIdAndMemberId(eventId, member.getId());
        for (CheckinMember checkinMember : checkinMemberList) {
            checkinMemberRepository.deleteById(checkinMember.getId());
        }

        // 약속 멤버 삭제
        eventMemberRepository.deleteById(eventMember.getId());
        eventMemberRepository.flush();

        // 자신과의 약속 or 마지막 멤버일 경우 약속이 삭제되도록하기
        List<EventMember> eventMemberList = eventMemberRepository.findAllByEventId(eventId);
        if (eventMemberList.size() == 0) {
            eventRepository.deleteById(eventId);
            return ResponseDto.success("약속에서 탈퇴했습니다.");
        }

        // 자신이 방장일 경우 방장권한을 다음으로 들어온 사람에게 위임한다.
        if (event.getMaster().getId().equals(member.getId())) {
            event.changeMaster(eventMemberList.get(0).getMember());
        }

        return ResponseDto.success("약속에서 탈퇴했습니다.");
    }

    /**
     * 체크인
     */
    public ResponseDto<?> checkin(Long eventId, HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 멤버 호출
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }

        // 약속 참여자 여부 확인
        if (eventMemberRepository.findByEventIdAndMemberId(eventId, member.getId()).isEmpty())
            return ResponseDto.fail("약속 참여자가 아닙니다.");

        // 중복 체크인 방지
        if (isPresentCheckinMember(eventId, member.getId()).getAttendance().equals(Attendance.ONTIME))
            return ResponseDto.fail("이미 체크인 했습니다.");

        // 약속 호출
        Event event = isPresentEvent(eventId);

        // 약속 두 시간 전부터 체크인 가능
        if (LocalDateTime.now().isBefore(event.getEventDateTime().minusHours(2)))
            return ResponseDto.fail("체크인은 2시간 전부터 가능합니다.");

        // 약속상태가 아직 ongoing(체크인 가능상태)인지 확인
        if (event.getEventStatus() == EventStatus.CLOSED)
            return ResponseDto.fail("체크인 가능 시간이 지났습니다.");

        // 체크인멤버 객체 호출
        CheckinMember checkinMember = isPresentCheckinMember(event.getId(), member.getId());

        // 체크인 시각에 따른 출석 상태 지정
        if (LocalDateTime.now().isBefore(event.getEventDateTime())) {
            checkinMember.setAttendance(Attendance.ONTIME);
        } else checkinMember.setAttendance(Attendance.LATE);

        checkinMemberRepository.save(checkinMember);

        // 해당 이벤트에 대한 체크인멤버 전체 호출
        List<CheckinMember> findCheckinMemberList = checkinMemberRepository.findAllByEventId(eventId);

        List<MemberResponseDto> memberResponseDtoList = new ArrayList<>();
        for (CheckinMember tempCheckinMember : findCheckinMemberList) {
            MemberResponseDto memberResponseDto = convertToDto(member, tempCheckinMember.getMember());
            memberResponseDto.setAttendance(tempCheckinMember.getAttendance());
            memberResponseDtoList.add(memberResponseDto);
        }
        return ResponseDto.success(memberResponseDtoList);
    }

    /**
     * 체크인 목록 조회
     */
    public ResponseDto<?> getCheckinMembers(Long eventId, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 멤버 호출
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }

        // 약속 참여자 여부 확인
        if (eventMemberRepository.findByEventIdAndMemberId(eventId, member.getId()).isEmpty())
            return ResponseDto.fail("약속 참여자가 아닙니다.");

        // 해당 이벤트에 대한 체크인멤버 전체 호출
        List<CheckinMember> findCheckinMemberList = checkinMemberRepository.findAllByEventId(eventId);

        List<MemberResponseDto> memberResponseDtoList = new ArrayList<>();
        for (CheckinMember tempCheckinMember : findCheckinMemberList) {
            MemberResponseDto memberResponseDto = convertToDto(member, tempCheckinMember.getMember());
            memberResponseDto.setAttendance(tempCheckinMember.getAttendance());
            memberResponseDtoList.add(memberResponseDto);
        }
        return ResponseDto.success(memberResponseDtoList);
    }

    /**
     * 약속 멤버 체크
     */
    public ResponseDto<?> chkEventMember(Long eventId, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 멤버 호출
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN");
        }

        EventMember eventMember = eventMemberRepository.findByEventIdAndMemberId(eventId, member.getId()).orElse(null);
        if (eventMember == null)
            return ResponseDto.success(false);
        return ResponseDto.success(true);
    }

    // 신용도, 포인트 증감
    // 호출하는 곳에서 얼마나 정보르 주는지에 따라 메소드가 달라 질 수 있음.
    @Transactional
    public boolean calculateCredit(Long eventId) {

        // 체크인 멤버리스트 들고오기 (체크인멤버 = 이벤트참여자)
        List<CheckinMember> findCheckinMemberList = checkinMemberRepository.findAllByEventId(eventId);

        // 참여자가 한명일 경우(자신과의 약속)
        if(findCheckinMemberList.size() == 1) {
            // 자신과의 약속 카운터 올려주기
            findCheckinMemberList.get(0).getMember().updateSelfEvent();
            return true;
        }
        else if (findCheckinMemberList.size() == 0) // 아마 동작하지는 않겟지만 확인용.
            return false;

        // 신용도, 포인트 결산
        double addCreditScore = 0;
        int point = 0;
        int done = 0;
        for(CheckinMember checkinMember : findCheckinMemberList) {
            // 이벤트 참여자가 잘 참여했는지 확인 (정상, 지각)
            switch (checkinMember.getAttendance().toString()) {
                case "ONTIME":
                    addCreditScore = MAG_DONE_CREDIT * 0.1 * (1);
                    point = checkinMember.getEvent().getPoint();
                    done = 1;
                    if(checkinMember.getMember().getPointOnDay() > 1000)
                        point = 0;  // 하루에 벌 수 있는 포인트는 1000점 제한
                    break;
                case "LATE":
                    addCreditScore = MAG_DONE_CREDIT * 0.1 * (0);
                    point = 0;
                    done = 1;
                    break;
                case "NOSHOW":
                    addCreditScore = MAG_DONE_CREDIT * 0.1 * (-1);
                    point = (checkinMember.getEvent().getPoint()*-1);   // 다시 물어봐야함 건만큼 뺏기는것이 맞는건가?
                    done = 0;
                    break;
            }
            // 신용도 업데이트
            checkinMember.getMember().updateCreditScore(Math.floor(addCreditScore * 10) / (10.0));
            // 약속 카운터 올리기
            checkinMember.getMember().updateNumOfDone(done);
            // 포인트 업데이트
            checkinMember.getMember().updatePoint(point);

        }
        // 결과 출력
        return true;

    }


    /**
     * 약속 컨펌(방장만 가능)
     */
    public ResponseDto<?> confirm(Long eventId, HttpServletRequest request) {

        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;

        // 엔티티 조회
        Member member = validateMember(request);
        if (null == member)
            return ResponseDto.fail("토큰이 유효하지 않습니다.");
        // 약속 호출
        Event event = isPresentEvent(eventId);

        if (null == event)
            return ResponseDto.fail("이벤트를 찾을 수 없습니다.");
        // 방장 여부 확인
        if (!isMaster(event, member))
            return ResponseDto.fail("방장이 아닙니다.");
        // 약속 시간 후 컨펌이 이루어지는지 확인
        if (LocalDateTime.now().isBefore(event.getEventDateTime())) {
            return ResponseDto.fail("아직 약속시간 전입니다. 약속시간이 지난 후 다시 시도해주세요.");
        }

        // 이미 종료시킨 약속에 대해서는 다시 종료처리 불가능
        if (event.getEventStatus() == EventStatus.CLOSED) {
            return ResponseDto.fail("이미 종료시킨 약속입니다.");
        }

        // 이벤트상태
        if(calculateCredit(eventId)) {
            event.confirm();
//            sseEmitterService.pubEventConfirm(event);
            return ResponseDto.success("약속을 확인했습니다. 더이상 체크인할 수 없습니다.");
        }
        else
            return ResponseDto.fail("약속완료 실패");
    }

    /**
     * 약속까지 남은 시간 알림
     */
    @Transactional
    public void eventAlarm() {
        LocalDateTime now = LocalDateTime.now().withNano(0);  // LocalDateTime에서 밀리세컨드 부분 제거
        List<EventSchedule> eventScheduleList = eventScheduleRepository.findAll();
        for (EventSchedule eventSchedule : eventScheduleList) {
            if (eventSchedule.getTargetTime().equals(now)) {
                AlertType type = null;
                if (eventSchedule.getBeforeTime() == BeforeTime.DAY) {
                    log.info("약속(ID: " + eventSchedule.getEvent().getId() + ")이 하루 남았습니다.");
                    type = AlertType.DAY;
                } else if (eventSchedule.getBeforeTime() == BeforeTime.HOUR) {
                    log.info("약속(ID: " + eventSchedule.getEvent().getId() + ")이 한 시간 남았습니다.");
                    type = AlertType.HOUR;
                } else if (eventSchedule.getBeforeTime() == BeforeTime.MINUTE) {
                    log.info("약속(ID: " + eventSchedule.getEvent().getId() + ")이 십 분 남았습니다.");
                    type = AlertType.MIN;
                }
                sseEmitterService.publishInScheduler(eventSchedule.getEvent().getId(), type);
                eventScheduleRepository.delete(eventSchedule);
            }
        }
    }

    /**
     * 약속 자동 컨펌(하루 경과 시)
     */
    public void scheduledConfirm() {
        LocalDateTime now = LocalDateTime.now().withNano(0);  // LocalDateTime에서 밀리세컨드 부분 제거
        List<Event> eventList = eventRepository.findAllByEventStatusAndEventDateTimeLessThanEqual(EventStatus.ONGOING, now.minusDays(1));
        for (Event event : eventList) {
            // 이벤트상태
            if(calculateCredit(event.getId())) {
                event.confirm();
                eventRepository.save(event);
                log.info("약속(ID: " + event.getId() + ")이 시간경과로 인해 자동 확인되었습니다.");
            }
        }
    }


    //== 추가 메서드 ==//


    // 방장 확인 api
    public ResponseDto<?> chkMaster(Long eventId, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        // 약속 호출
        Event event = isPresentEvent(eventId);
        if (null == event) {
            return ResponseDto.fail("ID_NOT_FOUND");
        }
        Member master = event.getMaster();
        return ResponseDto.success(MasterInfoResponseDto.builder()
                .id(master.getId())
                .nickname(master.getNickname())
                .build());
    }

    // 방장확인 api2
    private boolean isMaster(Event event, Member member) {
        return event.getMaster().getId().equals(member.getId());
    }

    // 방장 위임
    @Transactional
    public ResponseDto<?> changeMaster(Long eventId, MasterRequestDto requestDto, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        // 방장확인 (약속방 참여자인지도 같이 확인 가능)
        Event event = isPresentEvent(eventId);
        if (null == event) {
            return ResponseDto.fail("약속을 찾을 수 없습니다");
        }
        if (!isMaster(event, member))
            return ResponseDto.fail("방장이 아닙니다.");

        Member target = memberRepository.findById(requestDto.getTargetId()).orElse(null);
        if (target == null)
            return ResponseDto.fail("해당 사용자를 찾을 수 없습니다.");

        if (Objects.equals(target.getId(), member.getId()))
            return ResponseDto.fail("본인에게 위임할 수 없습니다.");

        // 위임할 사람이 참여자인지 체크
        EventMember eventMember = isPresentEventMember(event, target);
        if (eventMember == null)
            return ResponseDto.fail("해당 사용자가 약속 참여자가 아닙니다.");

        event.changeMaster(target);

        return ResponseDto.success("위임완료");

    }

    // 맴버 추방
    @Transactional
    public ResponseDto<?> kickMember(Long eventId, MasterRequestDto requestDto, HttpServletRequest request) {
        ResponseDto<?> chkResponse = validateCheck(request);
        if (!chkResponse.isSuccess())
            return chkResponse;
        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
        assert member != null;  // 동작할일은 없는 코드

        // 방장확인 (약속방 참여자인지도 같이 확인 가능)
        Event event = isPresentEvent(eventId);
        if (null == event) {
            return ResponseDto.fail("약속을 찾을 수 없습니다");
        }
        if (!isMaster(event, member))
            return ResponseDto.fail("방장이 아닙니다.");

        Member target = memberRepository.findById(requestDto.getTargetId()).orElse(null);
        if (target == null)
            return ResponseDto.fail("해당 사용자를 찾을 수 없습니다.");

        if (Objects.equals(target.getId(), member.getId()))
            return ResponseDto.fail("본인을 추방시킬 수 없습니다.");

        // 추방당할 사람이 참여자인지 체크
        EventMember eventMember = isPresentEventMember(event, target);
        if (eventMember == null)
            return ResponseDto.fail("해당 사용자가 약속 참여자가 아닙니다.");

        eventMemberRepository.deleteById(eventMember.getId());
        // 체크인에서도 지우기
        List<CheckinMember> checkinMemberList = checkinMemberRepository.findByEventIdAndMemberId(eventId, target.getId());
        for (CheckinMember checkinMember : checkinMemberList) {
            checkinMemberRepository.deleteById(checkinMember.getId());
        }

        return ResponseDto.success("추방완료");

    }

    /**
     * eventMember 유효성 검사
     */
    public boolean validateEventMember(Event event, Member member) {
        Optional<EventMember> findEventMember = eventMemberRepository.findByEventIdAndMemberId(event.getId(), member.getId());
        return findEventMember.isEmpty();
    }

    /**
     * eventMember 호출
     */
    public EventMember isPresentEventMember(Event event, Member member) {
        Optional<EventMember> optionalEventMember = eventMemberRepository.findByEventIdAndMemberId(event.getId(), member.getId());
        return optionalEventMember.orElse(null);
    }

    /**
     * 멤버 유효성 검사
     */
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    /**
     * 입력값 형변환 String to LocalDateTime
     */
    public LocalDateTime stringToLocalDateTime(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        return LocalDateTime.parse(dateStr, formatter);
    }

    /**
     * 약속 호출
     */
    @Transactional(readOnly = true)
    public Event isPresentEvent(Long id) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        return optionalEvent.orElse(null);
    }

    /**
     * 체크인멤버 호출
     */
    @Transactional(readOnly = true)
    public CheckinMember isPresentCheckinMember(Long eventId, Long memberId) {
        List<CheckinMember> optionalCheckinMember = checkinMemberRepository.findByEventIdAndMemberId(eventId, memberId);

        if(optionalCheckinMember.size() != 0)
            return optionalCheckinMember.get(0);
        return null;
    }

    /**
     * 멤버 호출 byId
     */
    @Transactional(readOnly = true)
    public Member isPresentMember(Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        return optionalMember.orElse(null);
    }

    /**
     * 멤버 호출 byNickname
     */
    @Transactional(readOnly = true)
    public Member isPresentMemberByNickname(String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        return optionalMember.orElse(null);
    }

    /**
     * Member를 MemberResponseDto로 변환
     */
    public MemberResponseDto convertToDto(Member owner, Member friend) {
        if(friend ==null) {//방장을 이벤트 멤버에 등록하는 경우
            return MemberResponseDto.builder()
                    .id(owner.getId())
                    .phoneNumber(owner.getPhoneNumber())
                    .email(owner.getEmail())
                    .nicknameByOwner(owner.getNickname())
                    .nicknameByFriend(null)
                    .credit(owner.getCredit())
                    .point(owner.getPoint())
                    .profileImageUrl(owner.getProfileImageUrl())
                    .build();
        }
        else {//방장 이외의 친구를 이벤트 멤버로 등록하는 경우
            String nicknameByOwner = null;
            Friend tempFriend = friendRepository.findByOwnerAndFriend(owner, friend).orElse(null);
            if (tempFriend != null)
                nicknameByOwner = tempFriend.getSecondName();
            return MemberResponseDto.builder()
                    .id(friend.getId())
                    .phoneNumber(friend.getPhoneNumber())
                    .email(friend.getEmail())
                    .nicknameByOwner(nicknameByOwner)
                    .nicknameByFriend(friend.getNickname())
                    .credit(friend.getCredit())
                    .point(friend.getPoint())
                    .profileImageUrl(friend.getProfileImageUrl())
                    .build();
        }
    }

    /**
     * Event를 EventListDto로 변환
     */
    public EventListDto convertToDto(Event event) {
        return EventListDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .eventDateTime(Time.serializeEventDate(event.getEventDateTime()))
                .place(event.getPlace())
                .weatherResponseDto(weatherService.getWeatherInfo(event))
                .memberCount(eventMemberRepository.findAllByEventId(event.getId()).size())
                .lastTime(Time.convertLocaldatetimeToTime(event.getEventDateTime()))
                .build();
    }

    /**
     * 토큰 유효성 검사
     */
    private ResponseDto<?> validateCheck(HttpServletRequest request) {

        // RefreshToken 및 Authorization 유효성 검사
        if (null == request.getHeader("RefreshToken") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        // token 정보 유효성 검사
        if (null == member) {
            return ResponseDto.fail("Token이 유효하지 않습니다.");
        }
        return ResponseDto.success(member);
    }
}