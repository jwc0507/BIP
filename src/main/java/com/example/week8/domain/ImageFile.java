package com.example.week8.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
// 이미지 관리를 위한 entity 현재 관계구조 x 만약 여러개의 사진을 넣는다면 식별관계 테이블 추가하고 추가 관리용으로 사용가능.
public class ImageFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private String imageName;

    @Column
    private String url;

}


