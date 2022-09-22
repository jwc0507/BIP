# Week8 실전프로젝트 

## 1주차. 

### DB, api설계, 기본 기능들 구현

9월 24일까지 로그인,회원가입,약속CRUD,친구페이지 기능 구현 완료 목표

#### 레포지토리 클론해갔을때 resourse 경로 아래에 application-aws.properties 파일 생성필수. 
내용은 개인 DM으로 드리겠습니다.

## 트러블 슈팅

####1. [ERR] unable to evaluate the expression method threw 'org.hibernate.lazyinitializationexception' exception

에러 사항 - 엔티티 컬럼 LAZY 이슈

SOLVE - 엔티티 객체를 불러올때 지연로딩을 해서 실제 사용할때 값을 알 수 없어 에러가 발생. 해당 ENTITIY만 EAGER로 변경.

####2. [ERR] org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags 

에러 사항 - 두개의 EAGER사용제약

SOLVE - 양방향 사용을 위해 EAGER 컬럼을 두개 생성하여 제약사항에 걸림. 정말 필요한 컬럼을 남기고 단방향 설계로 수정.
