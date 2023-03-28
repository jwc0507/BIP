package com.example.week8.domain;

import com.example.week8.dto.response.WeatherResponseDto;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class WeatherInfo {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Event event;
    @Column
    private String name;
    @Column
    private String temperature;
    @Column
    private String maxTemp;
    @Column
    private String minTemp;
    @Column
    private String probability;
    @Column
    private String sky;
    @Column
    private String skyDesc;
    @Column
    private String icon;

    public void update(WeatherResponseDto responseDto) {
        this.name = responseDto.getName();
        this.temperature = responseDto.getTemperature();
        this.maxTemp = responseDto.getMaxTemp();
        this.minTemp = responseDto.getMinTemp();
        this.probability = responseDto.getProbability();
        this.sky = responseDto.getSky();
        this.skyDesc = responseDto.getSkyDesc();
        this.icon = responseDto.getIcon();
    }
}