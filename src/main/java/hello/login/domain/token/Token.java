package hello.login.domain.token;

import hello.login.web.dto.KakaoTokenDTO;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Token {

    @Id
    private Long id;

    //private String accessToken;

    private String refreshToken;

    //private Integer expiresIn;

    private Integer refreshExpiresIn;

    public Token() {
    }

    public Token(Long id, KakaoTokenDTO requestToken) {
        this.id = id;
        //this.accessToken = requestToken.getAccess_token();
        this.refreshToken = requestToken.getRefresh_token();
        //this.expiresIn = requestToken.getExpires_in();
        this.refreshExpiresIn = requestToken.getRefresh_token_expires_in();
    }

    // 사용자 토큰 갱신 메서드
    // JPA 를 통하여 토큰 조회 후 호출됨
    public void update(KakaoTokenDTO newToken) {
        //this.accessToken = newToken.getAccess_token();
        this.refreshToken = newToken.getRefresh_token();
        //this.expiresIn = newToken.getExpires_in();
        this.refreshExpiresIn = newToken.getRefresh_token_expires_in();
    }
}