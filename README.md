<div align="center">
  <img src="https://github.com/user-attachments/assets/b6ef8cd5-0798-4fc0-b22a-6b13a9c9c315" width="500" alt="limelight_logo">
</div>

# 🎬 LimeLight

<div align="center">

![License](https://img.shields.io/badge/license-MIT-green) ![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white) ![PHP](https://img.shields.io/badge/PHP-777BB4?style=flat&logo=php&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)

> **"영화로 일상을 비추다"**
>
> **Web & App 크로스 플랫폼 영화 예매 서비스**

</div>

---

## 📖 프로젝트 개요

**limelight**는 19세기 극장에서 무대 위 주인공을 강조하는 스포트라이트 용도로 널리 쓰였습니다. 이 조명 장치는 사라졌지만, "in the limelight"라는 표현은 그대로 남아 오늘날 "각광받다"라는 관용구로 굳어졌습니다.

영화 예매 서비스 **LimeLight**는 서비스명의 의미와 어원을 시각적으로 전달하기 위해 브랜드 컬러를 **Neon Lime**으로 정의했습니다. 또한 영화관의 어두운 조명 환경을 고려하여 다크 모드 UI를 채택했습니다.

**LimeLight**는 안드로이드 App과 Web이 하나의 MySQL 데이터베이스를 공유하여 사용자가 어디서든 끊김 없이 예매 서비스를 이용할 수 있도록 구현된 **1인 풀스택 개발 프로젝트**입니다.

* **웹 서비스 배포 링크:** [http://devmanjoo.mycafe24.com/](http://devmanjoo.mycafe24.com/)
* **프로젝트 기간:** 2025.11.05 ~ 2025.12.10
* **개발 인원:** 1인 (Design, Frontend, Backend, Android, DB)

---

## ✨ 핵심 기능

### 📱 사용자 기능 - App & Web 공통

* **통합 계정:** 앱에서 가입한 계정으로 웹 로그인 가능<br><br>
  <img src="https://github.com/user-attachments/assets/8b7d1ca2-6a68-4cd0-acb0-6e37703ba5cb" height="400" alt="로그인"> <img src="https://github.com/user-attachments/assets/57a7c87c-22c8-4c97-9e94-b693be2de64f" height="400" alt="회원가입">

* **영화 예매:** 극장 > 날짜 > 영화 > 시간 > 인원 선택 프로세스<br><br>
  <img src="https://github.com/user-attachments/assets/0fd773dc-b920-450c-b167-b8f8fd912c22" height="400" alt="예매"> <img src="https://github.com/user-attachments/assets/940e8396-f306-4e29-8da4-436dfdf9aa8a" height="400" alt="예매 성공"> <img src="https://github.com/user-attachments/assets/757fcef9-f534-44f2-9518-66ada9bccd98" height="400" alt="내역"> <img src="https://github.com/user-attachments/assets/a1602964-0958-4500-920a-5a05f18794dc" height="400" alt="로그아웃 예매">

* **무비차트 및 검색:** 현재 상영작/상영 예정작 조회 및 영화 검색 기능<br><br>
  <img src="https://github.com/user-attachments/assets/6b3c6f20-001a-4b23-be86-e57342f3f45a" height="400" alt="홈"> <img src="https://github.com/user-attachments/assets/12d0a1e0-90ed-4165-8c3c-c986a2af09d2" height="400" alt="검색">

* **게이미피케이션:** 관람 횟수에 따른 레벨 업 시스템 및 통계 제공<br><br>
  <img src="https://github.com/user-attachments/assets/655f8be4-55bb-4ce6-a061-15142b7af3af" height="400" alt="프로필">

* **커뮤니티:** 한줄평 작성 및 'My List(찜하기)' 기능<br><br>
  <img src="https://github.com/user-attachments/assets/b2bfcc6c-32b0-4e40-91d2-5805624d9cb9" height="400" alt="한줄평"> <img src="https://github.com/user-attachments/assets/6f8a0599-1160-427b-99a4-57899192749f" height="400" alt="로그아웃 한줄평"> <img src="https://github.com/user-attachments/assets/b6772170-98d1-4018-81bc-80fa0b3dd6f9" height="400" alt="좋아요">

* **결제 시스템:** 조조, 청소년, 우대 등 조건별 요금 자동 계산<br><br>
  <img src="https://github.com/user-attachments/assets/1777487a-f442-479c-9be7-d364d1ebba57" height="400" alt="가격">

### 🖥️ 관리자 기능 - Web Only
* **대시보드:** 영화, 지점, 상영 스케줄 데이터의 통합 관리
* **데이터 보호 (Soft Delete):** 영화나 지점 삭제 시 DB에서 완전히 지우지 않고 `is_deleted` 플래그를 사용하여 기존 고객의 예매 내역을 안전하게 보존
* **스케줄링:** 날짜별 상영 시간표 등록 및 삭제 시스템

---

## 🛠️ 개발 환경

### **Android App**
* **IDE:** Android Studio Hedgehog | 2023.1.1
* **Language:** Java
* **Build System:** Gradle 8.2
* **Target SDK:** 34 (Android 14)
* **Min SDK:** 21
* **Libraries:** Retrofit2, OkHttp3, Glide, Gson, Commons-io

### **Web & Backend**
* **Server:** PHP (RESTful API 설계)
* **Database:** MySQL (MariaDB)
* **Frontend:** HTML5, CSS3, JavaScript
* **Hosting:** Cafe24 (SFTP 배포)

---

## 🔒 보안 및 아키텍처

* **API Security:** `API Key` 인증 방식을 도입하여 인가되지 않은 외부 접근 차단
* **Database Security:**
    * `mysqli_real_escape_string`을 통한 SQL Injection 방지
    * 사용자 비밀번호 Hashing 저장
* **Config Management:**
    * Android: `local.properties`를 통해 API Key 은닉
    * Web: `db_secret.php` 분리 및 접근 제한

---

## 📂 데이터베이스 설계

주요 테이블 구성:
* `users`: 사용자 정보
* `movies`: 영화 상세 정보
* `theaters`: 극장 지점 정보
* `showtimes`: 상영 스케줄
* `bookings`: 예매 내역
* `reviews`: 한줄평
* `wishlist`: 찜한 영화 목록

---

## 📝 라이선스

이 프로젝트는 MIT License를 따릅니다.
자세한 내용은 `LICENSE` 파일을 참고하세요.

Copyright (c) 2025 Minju Kim
