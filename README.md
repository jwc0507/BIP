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

#### 3. [issue] 카카오 로그인 구현시 넘어오는 값으로 회원가입 구현불가

- 제약 사항 : 카카오 api에서 넘어온 info에 유저인증 식별자로 사용하고 있던 전화번호가 들어있지 않음. 회원가입과 로그인 할 때 토큰생성 불가능.
- SOLVE : 유저인증을 기본키 id로 할 수 있게 변경함. 변경함으로써 닉네임이나 전화번호등 겉으로 드러나는 정보가 수정될 때 토큰 재생성을 할 필요가 없음

#### 4. [issue] oauth로그인했을때 토큰인증이 필요한 api호출시 401에러 (인증실패)를 반환

- 에러 사항 : 로컬테스트로 코드 테스트 결과 소셜로그인회원의 로그인이후 securitycontextholder에 jwt토큰을 같이 넣어서 유저정보를 넣지 않던 것을 확인, 
추가로 header prefix형식도 기존 filter에서 체크하는 형식과도 같지 않던 부분 확인함.
- SOLVE : 토큰 생성부분을 contextholder에 유저정보를 넣는 부분보다 앞으로 당겨줌. 토큰 생성부에서 붙이는 prefix값을 BEARER에서 Bearer로 수정해줌. (이 부분은 filter에서 prefix부분만 uppercase 또는 lowercase형식으로 모두 받을 수 있게 수정해도 괜찮을 것 같음) 
