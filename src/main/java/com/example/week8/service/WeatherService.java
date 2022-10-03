/*
package com.example.week8.service;


import com.example.week8.dto.response.ResponseDto;
import com.example.week8.dto.response.WeatherResponseDto;
import com.example.week8.utils.OpenWeather;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.net.URLEncoder;
import java.util.Objects;


@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {

    @Value("${weather.accessKey}")
    private String apiKey;
    private final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    // 날씨 단건조회
    public ResponseDto<?> getLocalWeather(String coordinate) {
        String[] splitCoordinate = coordinate.split(",");

        double lat = Double.parseDouble(splitCoordinate[0]);
        double lon = Double.parseDouble(splitCoordinate[1]);
        OpenWeather response;
        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        try {
            urlBuilder.append("?" + URLEncoder.encode("lat", "UTF-8") + "=" + lat);
            urlBuilder.append("&" + URLEncoder.encode("lon") + "=" + lon);
            urlBuilder.append("&" + URLEncoder.encode("appid", "UTF-8") + "=" + apiKey);

            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.getForObject(urlBuilder.toString(), OpenWeather.class);
        } catch (Exception e) {
            return ResponseDto.fail(e);
        }
        String name = response.getName();
        if(Objects.equals(name, ""))
            name = "주변 대도시 정보 없음";
        return ResponseDto.success(WeatherResponseDto.builder()
                .name(name)
                .temperature(convertFaToCel(response.getMain().getTemp()) + "°C")
                .maxTemp(convertFaToCel(response.getMain().getTemp_max()) + "°C")
                .minTemp(convertFaToCel(response.getMain().getTemp_min()) + "°C")
                .probability(response.getClouds().getAll() + "%")
                .icon(response.getWeather().get(0).getIcon())
                .sky(convertSky(response.getWeather().get(0).getMain()))
                .skyDesc(response.getWeather().get(0).getDescription())
                .build());

    }

    private String convertFaToCel(float fahrenheit) {
        return String.format("%.2f", (fahrenheit - 273.15f) * 100 / 100.0f);
    }

    private String convertSky(String main) {
        switch (main) {
            case "Clear":
                return "맑음";
            case "Clouds":
                return "흐림";
            case "Rain":
                return "비";
            case "Snow":
                return "눈";
            case "Drizzle":
                return "이슬비";
            case "Thunderstorm":
                return "천둥번개";
            case "Mist":
                return "약한 안개";
            case "Smoke":
                return "옅은 안개";
            case "Haze":
                return "안개";
            case "Dust":
                return "약한 황사";
            case "Fog":
                return "짙은 안개";
            case "Sand":
                return "짙은 황사";
            case "Ash":
                return "재 주의";
            case "Squall":
                return "돌풍";
            case "Tornado":
                return "폭풍";

            default:
                return "알 수 없음";
        }
    }

 */
//
//    /*
//     * POP	강수확률	 %
//     * PTY	강수형태	코드값
//     * R06	6시간 강수량	범주 (1 mm)
//     * REH	습도	 %
//     * S06	6시간 신적설	범주(1 cm)
//     * SKY	하늘상태	코드값
//     * T3H	3시간 기온	 ℃
//     * TMN	아침 최저기온	 ℃
//     * TMX	낮 최고기온	 ℃
//     * UUU	풍속(동서성분)	 m/s
//     * VVV	풍속(남북성분)	 m/s
//     */
//
//
//    private String getTime() {
//        int currentHour = LocalDateTime.now().getHour();
//
//        // 23 0 1 : 0 , 2 3 4 : 1 , 5 6 7 : 2 , 8 9 10 : 3 , 11 12 13 : 4 , 14 15 16 : 5 , 17 18 19 : 6 , 20 21 22 : 7
//        if (currentHour == 23)
//            currentHour = 0;
//        int convertOption = (int)Math.round(currentHour/3.0);
//
//        String searchTime = "";
//        switch (convertOption) {
//            case (0) : // 23시의 정보 제공
//                searchTime += "2300";
//                break;
//            case (1) : // 2시의 정보 제공
//                searchTime += "0200";
//                break;
//            case (2) : // 5시의 정보 제공
//                searchTime += "0500";
//                break;
//            case (3) : // 8시의 정보 제공
//                searchTime += "0800";
//                break;
//            case (4) : // 11시의 정보 제공
//                searchTime += "1100";
//                break;
//            case (5) : // 14시의 정보 제공
//                searchTime += "1400";
//                break;
//            case (6) : // 17시의 정보 제공
//                searchTime += "1700";
//                break;
//            case (7) : // 20시의 정보 제공
//                searchTime += "2000";
//                break;
//        }
//        return searchTime;
//    }

//    private XY convertXY (int mode, String coordinate) {
//        String[] splitCoordinate = coordinate.split(",");
//
//        double latX = Double.parseDouble(splitCoordinate[0]);
//        double lngY = Double.parseDouble(splitCoordinate[1]);
//
//        double RE = 6371.00877; // 지도반경
//        double GRID = 5.0; // 격자간격 (km)
//        double SLAT1 = 30.0; // 표준위도 1
//        double SLAT2 = 60.0; // 표준위도 2
//        double OLON = 126.0; // 기준점 경도
//        double OLAT = 38.0; // 기준점 위도
//        double XO = 43; // 기준점 X좌표
//        double YO = 136; // 기준점 Y좌표
//        double DEGRAD = Math.PI/ 180.0;
//        double re = RE/GRID;
//        double slat1 = SLAT1 * DEGRAD;
//        double slat2 = SLAT2 * DEGRAD;
//        double olon = OLON * DEGRAD;
//        double olat = OLAT * DEGRAD;
//
//        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
//        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
//        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
//        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
//        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
//        ro = re * sf / Math.pow(ro, sn);
//        XY xy = new XY();
//
//        if (mode == 0) {
//            double ra = Math.tan(Math.PI * 0.25 + (latX) * DEGRAD * 0.5);
//            ra = re * sf / Math.pow(ra, sn);
//            double theta = lngY * DEGRAD - olon;
//            if (theta > Math.PI) theta -= 2.0 * Math.PI;
//            if (theta < -Math.PI) theta += 2.0 * Math.PI;
//            theta *= sn;
//
//            xy.nX = Math.floor(ra * Math.sin(theta) + XO + 0.5);
//            xy.nY = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);
//        }
//
//        return xy;
//    }
//}
//class XY  {
//    public double nX;
//    public double nY;
//}
