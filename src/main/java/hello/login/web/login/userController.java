package hello.login.web.login;

import hello.login.service.KakaoUserService;
import hello.login.web.dto.KakaoTokenDTO;
import hello.login.web.dto.KakaoUserInfoDTO;
import hello.login.web.login.kakao.KakaoLoginApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalTime;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class userController {

    private final KakaoUserService kakaoUserService;
    private final KakaoLoginApi kakaoLoginApi;

    @GetMapping("/login/kakao")
    public String kakaoLogin(@RequestParam(value = "code") String code, //Authorization_Code, 인가 코드
                             @RequestParam(value = "error", required = false) String error,
                             @RequestParam(value = "error_description", required = false) String error_description,
                             HttpServletResponse response) throws Exception {

        // 에러 발생 시
        if (error != null) {
            /**
             * 참고합시다
             * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-code
             * 에러 페이지 처리도 고민해봐야함
             */

            return "home";
        }

        /**
         * 1. 카카오 인증 서버에 토큰 요청 (HTTP 요청)
         * 참고 1
         * https://github.com/JSBeatCode/kakaologin/blob/main/src/main/java/com/kakao/app/KakaoAPI.java
         * 참고 2
         * https://blog.naver.com/PostView.nhn?blogId=rhkrehduq&logNo=221526106085&parentCategoryNo=&categoryNo=22&viewDate=&isShowPopularPosts=true&from=search
         * 찾아보니 "java.net.URL", "HttpURLConnection" 을 이용하여 토큰을 요청할 수 있음
         */

        // 계속 생성해서 사용하는 것이 맞는지 아니면 스프링에 등록하고 주입 받아서 쓴는 것이 맞는지?
        // KakaoLoginApi kakaoLoginApi = new KakaoLoginApi();
        KakaoTokenDTO token = kakaoLoginApi.requestToken(code);

        //2. 사용자가 수락한 정보를 요청 (HTTP 요청 => 닉네임, 이메일)
        KakaoUserInfoDTO userInfo = kakaoLoginApi.requestUserInfo(token.getAccess_token());

        //3. 서비스로직 처리, 가입 환영 페이지?
        //상황 => 'token' 과 '카카오 사용자 정보' 를 받음
        kakaoUserService.login(userInfo, token);

        // 사용자에게 access_token 내려주기 위한 쿠키
        Cookie cookie = new Cookie("access_token_1", token.getAccess_token());
        cookie.setMaxAge(token.getExpires_in());
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        // 쿠키에 시간 정보를 주지 않으면 세션 쿠키가 됨(브라우저 종료 시 사라지는 쿠키)
        response.addCookie(cookie);

        return "redirect:/";
    }

    @PostMapping("/logout/kakao")
    public String kakaoLogout(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Cookie[] cookies = request.getCookies();

        // 쿠키에서 사용자에게 내려준 access_token 을 찾음
        for (Cookie cookie : cookies) {

            // 쿠키에 access_token 있고 그 token 이 Authorization_server 에 요청하여 유효할 경우에
            // 사용자 로그 아웃을 함
            if (cookie.getName().equals("access_token_1")) {
                if (kakaoLoginApi.requestTokenInfo(cookie.getValue())) {
                    KakaoUserInfoDTO kakaoUserInfo = kakaoLoginApi.requestUserInfo(cookie.getValue());
                    kakaoUserService.logout(kakaoUserInfo);
                    kakaoLoginApi.requestLogout(cookie.getValue());
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }

        return "redirect:/";
    }
}