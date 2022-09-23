package com.example.week8.time;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Time {
    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
    public static String convertLocaldatetimeToTime(LocalDateTime localDateTime) {
        // 현재 시각
        LocalDateTime now = LocalDateTime.now();

        long tempDiffTime = localDateTime.until(now, ChronoUnit.SECONDS);
        if (tempDiffTime > 0) return "이미 지난 약속입니다.";

        // 현재(now) - 입력값(localDateTime; 약속시간)
        // 즉, 음수이므로 절댓값 처리
        long diffTime = Math.abs(localDateTime.until(now, ChronoUnit.SECONDS));

        String msg = null;
        if (diffTime < TIME_MAXIMUM.SEC){
            return diffTime + "초 후";
        }
        diffTime = diffTime / TIME_MAXIMUM.SEC;
        if (diffTime < TIME_MAXIMUM.MIN ) {
            return diffTime + "분 후";
        }
        diffTime = diffTime / TIME_MAXIMUM.MIN;
        if (diffTime < TIME_MAXIMUM.HOUR) {
            return diffTime + "시간 후";
        }
        diffTime = diffTime / TIME_MAXIMUM.HOUR;
        if (diffTime < TIME_MAXIMUM.DAY) {
            return diffTime + "일 후";
        }
        diffTime = diffTime / TIME_MAXIMUM.DAY;
        if (diffTime < TIME_MAXIMUM.MONTH) {
            return diffTime + "개월 후";
        }
        diffTime = diffTime / TIME_MAXIMUM.MONTH;
        return diffTime + "년 후";
    }

    public static long getLastTime(LocalDateTime localDateTime) {
        // 현재 시각
        LocalDateTime now = LocalDateTime.now();

        // 현재(now) - 입력값(localDateTime; 약속시간)
        return localDateTime.until(now, ChronoUnit.SECONDS);
    }
}
