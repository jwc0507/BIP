# Week8 실전프로젝트 

## 1주차. 

### DB, api설계, 기본 기능들 구현

9월 24일까지 로그인,회원가입,약속CRUD,친구페이지 기능 구현 완료 목표

#### 레포지토리 클론해갔을때 resourse 경로 아래에 application-aws.properties 파일 생성필수. 
내용은 개인 DM으로 드리겠습니다.

## 트러블 슈팅

#### 1. [ERR] unable to evaluate the expression method threw 'org.hibernate.lazyinitializationexception' exception

 - 에러 사항 : 엔티티 컬럼 LAZY 이슈
 - SOLVE : 엔티티 객체를 불러올때 지연로딩을 해서 실제 사용할때 값을 알 수 없어 에러가 발생. 해당 ENTITIY만 EAGER로 변경.

#### 2. [ERR] org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags 

- 에러 사항 : 두개의 EAGER사용제약
- SOLVE : 양방향 사용을 위해 EAGER 컬럼을 두개 생성하여 제약사항에 걸림. 정말 필요한 컬럼을 남기고 단방향 설계로 수정.

#### 3. [etc] 카카오 로그인 구현시 넘어오는 값으로 회원가입 구현불가

- 제약 사항 : 카카오 api에서 넘어온 info에 유저인증 식별자로 사용하고 있던 전화번호가 들어있지 않음. 회원가입과 로그인 할 때 토큰생성 불가능.
- SOLVE : 유저인증을 기본키 id로 할 수 있게 변경함. 변경함으로써 닉네임이나 전화번호등 겉으로 드러나는 정보가 수정될 때 토큰 재생성을 할 필요가 없음

#### 4. [etc] 

- 제약 사항 : 
- SOLVE : 
