package com.example.week8.repository;

import com.example.week8.domain.Event;
import com.example.week8.domain.WeatherInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherInfoRepository extends JpaRepository<WeatherInfo, Event> {
}
