package hello.login.web;

import hello.login.web.dto.KakaoUserInfoDTO;
import hello.login.web.dto.LoginUserInfoDTO;
import hello.login.web.dto.UserRequestTokenDTO;
import hello.login.web.login.kakao.KakaoLoginApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final KakaoLoginApi kakaoLoginApi;

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) throws Exception {

        // 쿠키의 access_token 을 통하여
        // 1. 어떤 쿠키 인지 확인
        UserRequestTokenDTO userRequestTokenDTO = new UserRequestTokenDTO();
        Optional<UserRequestTokenDTO> userRequestTokenOptional = userRequestTokenDTO.getRequestToken(request.getCookies());

        // 쿠키에 토큰이 없는 경우에 로그인 페이지를 사용자에게 내려줌
        if (userRequestTokenOptional.isEmpty()) {
            setHomeModel(model);
            return "home";
        }

        UserRequestTokenDTO userToken = userRequestTokenOptional.get();

        // 2. Authorization_Server 에 access_token 의 유효성을 검증 받음
        if (kakaoLoginApi.requestTokenInfo(userToken.getAccessToken())) {

            // 3. 받아온 정보를 통하여 loginHome 에 보일 사용자 정보를 가져옴
            KakaoUserInfoDTO kakaoUserInfo = kakaoLoginApi.requestUserInfo(userToken.getAccessToken());

            LoginUserInfoDTO user = new LoginUserInfoDTO(kakaoUserInfo.getEmail());

            model.addAttribute("user", user);
        }
        else {
            // 유효하지 않은 토큰일 경우 로그인 페이지로 유도함
            setHomeModel(model);
            return "home";
        }

        return "loginHome";
    }

    private void setHomeModel(Model model) {
        model.addAttribute("response_type", "code");
        // 상수 처리하고 가져다 쓰는게 더 좋을 것 같음
        model.addAttribute("client_id", "e1a284b69d3496bee515c4e8f9bb6e5a");
        model.addAttribute("redirect_uri", "http://localhost:8080/user/login/kakao");
    }
}