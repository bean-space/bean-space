# 빈 공간 - Bean Space
 > 개발 기간: 2024.07 ~ 2024.08

## 배포 주소
- 개발 버전: https://www.thebean.space
- 프론트 서버: https://bean-space-front.vercel.app

## 팀원 소개

| 이무준 | 이수진 | 박주빈 | 임상은 |
| --- | --- | --- | ---|
| ![image](https://github.com/user-attachments/assets/e7f0e78c-aaa6-45f6-8823-8a3d6d8cb886) | ![image](https://github.com/user-attachments/assets/3f736701-1c2e-4600-9f6f-92475c8b43c8) | ![image](https://github.com/user-attachments/assets/6679fac0-d29c-4e0b-9982-935645cd2a4d) |![image](https://github.com/user-attachments/assets/365aa961-2cf3-4f82-82f9-f969bb00d9e2)  |
| [Moo-moo-11](https://github.com/Moo-moo-11) | [devitssu](https://github.com/devitssu) | [DanDanjoo](https://github.com/DanDanjoo) | [sangeuuun](https://github.com/sangeuuun) |


## 📖 프로젝트 개요   
프로젝트 주제는 숙박 예약 어플리케이션으로 여행, 출장, 휴가 등 다양한 이유로 숙박 시설을 사용하는 사람들을 위해서 국내의 숙박 시설을 한 눈에 확인하고 예약할 수 있는 서비스를 만들었습니다.

## 🚀 주요 기능
🌟 **공간 검색 및 예약**
- 원하는 날짜와 가격, 편의시설에 맞는 숙소를 필터링 검색
![image](https://github.com/user-attachments/assets/19a51490-2aad-4fd5-9d34-01cf7a9f5654)
- 별점 순, 가격순, 최신 등록 순, 예약 많은 순 정렬
- 쿠폰을 발급받아 할인된 가격으로 예약 가능
![image](https://github.com/user-attachments/assets/41bb71ef-7dc8-401e-a816-2d04250dcbc7)

🌟 **리뷰 작성**
- 체크아웃 시간 이후 리뷰 작성 가능
- 공간 상세 페이지에서 리뷰 확인 가능
![image](https://github.com/user-attachments/assets/e730f9ae-b82d-4fa3-a628-b17ea14e6cfb)

🌟 **호스트 기능**
- 메뉴탭에서 호스트로 전환 가능
<img width="709" alt="image" src="https://github.com/user-attachments/assets/5c265ddd-6cdb-46d3-92e9-78c6ec2fd07d">

- 공간 등록, 삭제, 예약확인
<img width="1362" alt="image" src="https://github.com/user-attachments/assets/e69ce4ce-7574-4fcb-aa46-81bef154c84e">



## 🏗 아키텍처
![image](https://github.com/user-attachments/assets/e3ab2671-0481-4556-8171-a0218724438d)

## 🛠 기술 스택
- **Front End** 

<img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white"> <img src="https://img.shields.io/badge/vite-646CFF?style=for-the-badge&logo=vite&logoColor=white"> <img src="https://img.shields.io/badge/vercel-000000?style=for-the-badge&logo=vercel&logoColor=white">

- **Back End** 

<img src="https://img.shields.io/badge/kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">

- **DB & Infra**

<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white"> <img src="https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazonwebservices&logoColor=white"> <img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"> <img src="https://img.shields.io/badge/grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white"> <img src="https://img.shields.io/badge/prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white">


- **Communication** 

<img src="https://img.shields.io/badge/slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">

   
 ## 💻 설치 방법

### 1. Install
```bash
git clone https://github.com/bean-space/bean-space.git
cd bean-space 
```

## 📂 프로젝트 구조

```
├── BeanSpaceApplication.kt
├── api
│   ├── HealthCheckController.kt
│   ├── admin
│   │   ├── AdminController.kt
│   │   ├── AdminService.kt
│   │   └── dto
│   │       ├── RequestAddSpaceResponse.kt
│   │       └── UpdateSpaceStatus.kt
│   ├── auth
│   │   ├── AuthController.kt
│   │   ├── AuthService.kt
│   │   └── dto
│   │       ├── GetNewAccessTokenRequest.kt
│   │       ├── LoginRequest.kt
│   │       ├── LoginResponse.kt
│   │       └── SignUpRequest.kt
│   ├── coupon
│   │   ├── CouponController.kt
│   │   ├── CouponService.kt
│   │   └── dto
│   │       ├── CouponRequest.kt
│   │       ├── CouponResponse.kt
│   │       └── UserCouponResponse.kt
│   ├── host
│   │   ├── HostController.kt
│   │   ├── HostService.kt
│   │   └── dto
│   │       ├── AddSpaceRequest.kt
│   │       └── UpdateSpaceRequest.kt
│   ├── image
│   │   ├── ImageController.kt
│   │   ├── ImageService.kt
│   │   └── dto
│   │       ├── PreSignedUrlRequest.kt
│   │       └── PreSignedUrlResponse.kt
│   ├── member
│   │   ├── MemberController.kt
│   │   ├── MemberService.kt
│   │   └── dto
│   │       ├── MemberProfileResponse.kt
│   │       ├── MemberReservationResponse.kt
│   │       ├── UpdateProfileRequest.kt
│   │       └── UpdateSocialUserInfoRequest.kt
│   ├── oauth
│   │   ├── KakaoOAuth2Client.kt
│   │   ├── OAuth2LoginController.kt
│   │   ├── OAuth2LoginService.kt
│   │   └── dto
│   │       ├── KakaoLoginUserInfoResponse.kt
│   │       ├── KakaoTokenResponse.kt
│   │       └── KakaoUserPropertiesResponse.kt
│   ├── reservation
│   │   ├── ReservationController.kt
│   │   ├── ReservationService.kt
│   │   └── dto
│   │       ├── ReservationRequest.kt
│   │       └── ReservationResponse.kt
│   └── space
│       ├── SpaceController.kt
│       ├── SpaceService.kt
│       └── dto
│           ├── AddReviewRequest.kt
│           ├── CompactSpaceResponse.kt
│           ├── HostResponse.kt
│           ├── OfferResponse.kt
│           ├── PopularKeywordsResponse.kt
│           ├── ReviewResponse.kt
│           ├── SpaceDetailResponse.kt
│           ├── SpaceResponse.kt
│           ├── SpaceResponseWithoutAddress.kt
│           └── UpdateReviewRequest.kt
├── domain
│   ├── common
│   │   └── BaseTimeEntity.kt
│   ├── coupon
│   │   ├── model
│   │   │   ├── Coupon.kt
│   │   │   └── UserCoupon.kt
│   │   └── repository
│   │       ├── CouponRepository.kt
│   │       ├── CouponRepositoryImpl.kt
│   │       ├── CustomCouponRepository.kt
│   │       ├── CustomUserCouponRepository.kt
│   │       ├── UserCouponRepository.kt
│   │       └── UserCouponRepositoryImpl.kt
│   ├── exception
│   │   ├── AuthenticationException.kt
│   │   ├── GlobalExceptionHandler.kt
│   │   ├── InvalidImageException.kt
│   │   ├── ModelNotFoundException.kt
│   │   ├── NoPermissionException.kt
│   │   └── dto
│   │       └── ErrorResponse.kt
│   ├── image
│   │   ├── model
│   │   │   ├── Image.kt
│   │   │   └── ImageType.kt
│   │   └── repository
│   │       └── ImageRepository.kt
│   ├── member
│   │   ├── model
│   │   │   ├── Member.kt
│   │   │   └── MemberRole.kt
│   │   └── repository
│   │       └── MemberRepository.kt
│   ├── reservation
│   │   ├── model
│   │   │   └── Reservation.kt
│   │   └── repository
│   │       └── ReservationRepository.kt
│   └── space
│       ├── model
│       │   ├── Address.kt
│       │   ├── Offer.kt
│       │   ├── Review.kt
│       │   ├── SearchKeyword.kt
│       │   ├── Space.kt
│       │   ├── SpaceOffer.kt
│       │   ├── SpaceStatus.kt
│       │   └── Wishlist.kt
│       └── repository
│           ├── OfferRepository.kt
│           ├── ReviewRepository.kt
│           ├── SearchKeywordQueryDslRepository.kt
│           ├── SearchKeywordQueryDslRepositoryImpl.kt
│           ├── SearchKeywordRepository.kt
│           ├── SpaceOfferRepository.kt
│           ├── SpaceQueryDslRepository.kt
│           ├── SpaceQueryDslRepositoryImpl.kt
│           ├── SpaceRepository.kt
│           └── WishListRepository.kt
└── infra
    ├── log
    │   └── LogFilter.kt
    ├── querydsl
    │   └── QueryDslConfig.kt
    ├── redis
    │   ├── RedisConfig.kt
    │   └── RedisUtils.kt
    ├── restclient
    │   └── RestClientConfig.kt
    ├── s3
    │   ├── S3Config.kt
    │   ├── S3Service.kt
    │   └── imagevalidator
    │       ├── ImageValidator.kt
    │       └── ValidationResult.kt
    ├── security
    │   ├── CustomAccessDeniedHandler.kt
    │   ├── CustomAuthenticationEntrypoint.kt
    │   ├── config
    │   │   ├── PasswordEncoderConfig.kt
    │   │   └── SecurityConfig.kt
    │   ├── dto
    │   │   └── UserPrincipal.kt
    │   └── jwt
    │       ├── JwtAuthenticationFilter.kt
    │       ├── JwtAuthenticationToken.kt
    │       └── JwtPlugin.kt
    └── swagger
        └── SwaggerConfig.kt
```

## 📌 환경설정
- Language: Kotlin
- IDE: Intellij
- SDK: Eclipse Temurin 18.0.2

## 📜 라이선스
이 프로젝트는 MIT 라이선스를 따릅니다. 자세한 내용은 [LICENSE](https://github.com/bean-space/bean-space-front/blob/dev/LICENSE) 파일을 참고하세요.
