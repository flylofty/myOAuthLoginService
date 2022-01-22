package hello.login.web.dto;

import lombok.Getter;

@Getter
public class KakaoUserInfoDTO {

    /**
     * 사용자 정보를 담는 DTO
     */

    private String email;

    public KakaoUserInfoDTO(String email) {
        this.email = email;
    }

//    private String kakaoNickname;
//    private String email;
//
//    public KakaoUserInfoDTO(String kakaoNickname, String email) {
//        this.kakaoNickname = kakaoNickname;
//        this.email = email;
//    }
}
