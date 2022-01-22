package hello.login.web.dto;

import lombok.Getter;

@Getter
public class KakaoTokenDTO {

    /**
     * 토큰 요청 시 데이터를 담는 DTO
     */

    private String access_token;
    private String token_type;
    private String refresh_token;
    private Integer expires_in;
    private String scope;
    private Integer refresh_token_expires_in;

    public KakaoTokenDTO() {}
}
