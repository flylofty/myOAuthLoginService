package hello.login.web.dto;

import lombok.Getter;

@Getter
public class LoginUserInfoDTO {

    /**
     * 로그인 성공한 사용자의 정보를 담는 DTO
     */

    private String email;

    public LoginUserInfoDTO(String email) {
        this.email = email;
    }
}
