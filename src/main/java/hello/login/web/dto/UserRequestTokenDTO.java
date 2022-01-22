package hello.login.web.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Getter
public class UserRequestTokenDTO {

    /**
     * access_token 과 login_type 정보를 담는 DTO
     * 쿠키를 통해 데이터를 담고 전달함
     */

    //JWT, KAKAO, NAVER
    private String loginType;
    private String accessToken;

    public UserRequestTokenDTO() {
        this.accessToken = "";
    }

    // 쿠키를 통하여 토큰을 있는지 판별 후 전달
    public Optional<UserRequestTokenDTO> getRequestToken(Cookie[] cookies) {

        if (cookies == null)
            return Optional.empty();

        // 해당 해시 맵은 다른 것으로 고려해봐야 할듯?!
        Map<String, String> loginTypes = new HashMap<>();

        //JWT, KAKAO, NAVER
        loginTypes.put("access_token_0", "JWT");
        loginTypes.put("access_token_1", "KAKAO");
        loginTypes.put("naver_token_2", "NAVER");

        for (Cookie cookie : cookies) {
            String key = cookie.getName();

            if (loginTypes.containsKey(key)) {
                this.loginType = loginTypes.get(key);
                this.accessToken = cookie.getValue();
                return Optional.of(this);
            }
        }

        return Optional.empty();
    }
}
