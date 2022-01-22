package hello.login.web.login.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.login.web.dto.KakaoTokenDTO;
import hello.login.web.dto.KakaoUserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class KakaoLoginApi {

    /**
     * documentation URL
     * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
     *
     * 현재까지 작성된 것
     * 인가 코드 받기, 토큰 받기, 연결 끊기, 토큰 정보 보기, 동의 철회하기, 로그 아웃
     *
     * 그렇지 않은 것
     * 카카오계정과 함께 로그아웃, 토큰 갱신하기, (동의 내역 확인 하기)
     */

    // 토큰 요청, try-catch 처리 확실히
    public KakaoTokenDTO requestToken(String code) throws Exception {
        String targetURL = "https://kauth.kakao.com/oauth/token";
        KakaoTokenDTO token;

        // Host: kauth.kakao.com, 연결 생성
        URL url = new URL(targetURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        // POST /oauth/token HTTP/1.1
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);

        // Content-type: application/x-www-form-urlencoded;charset=utf-8
        httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // Parameters
        // grant_type, client_id, redirect_uri, code
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
        String sb = "grant_type=authorization_code" +
                "&client_id=e1a284b69d3496bee515c4e8f9bb6e5a" +
                "&redirect_uri=http://localhost:8080/user/login/kakao" +
                "&code=" + code;
        bw.write(sb);
        bw.flush();

        // JSON Parsing
        String messageBody = StreamUtils.copyToString(httpURLConnection.getInputStream(), StandardCharsets.UTF_8);

//        int responseCode = httpURLConnection.getResponseCode();
//        log.info("requestToken responseCode = {}", responseCode);

        bw.close();

        return new ObjectMapper().readValue(messageBody, KakaoTokenDTO.class);
    }

    // 사용자 정보 요청
    public KakaoUserInfoDTO requestUserInfo(String accessToken) throws Exception {

        String targetURL = "https://kapi.kakao.com/v2/user/me";

        URL url = new URL(targetURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        // GET/POST /v2/user/me HTTP/1.1
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);

        // Content-type: application/x-www-form-urlencoded;charset=utf-8
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        String messageBody = StreamUtils.copyToString(httpURLConnection.getInputStream(), StandardCharsets.UTF_8);

//        int responseCode = httpURLConnection.getResponseCode();
//        log.info("requestUserInfo responseCode = {}", responseCode);

        return getKakaoUserInfo(messageBody);
    }

    // 연결 끊기
    public void requestUserDisconnect(String accessToken) throws Exception {
        
        String targetURL = "https://kapi.kakao.com/v1/user/unlink";

        URL url = new URL(targetURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        // GET/POST /v2/user/me HTTP/1.1
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);

        // Content-type: application/x-www-form-urlencoded;charset=utf-8
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

//        String messageBody = StreamUtils.copyToString(httpURLConnection.getInputStream(), StandardCharsets.UTF_8);
//        int responseCode = httpURLConnection.getResponseCode();
    }

    // 동의 철회하기
    public void requestUserWithdrawConsent(String accessToken) throws Exception {

        String targetURL = "https://kapi.kakao.com/v2/user/revoke/scopes";
        URL url = new URL(targetURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        // POST /v2/user/revoke/scopes
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);

        httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));

         //동의 항목 중, 필수 항목은 철회 요청을 할 수 없음
         //필수 항목 철회 시, 403 응답 코드를 받음
        bw.write("scopes=[\"account_email\"]");
        bw.flush();

//        int responseCode = httpURLConnection.getResponseCode();
//        String messageBody = StreamUtils.copyToString(httpURLConnection.getInputStream(), StandardCharsets.UTF_8);
    }

    // 로그 아웃
    public void requestLogout(String accessToken) throws Exception {
        String targetURL = "https://kapi.kakao.com/v1/user/logout";

        URL url = new URL(targetURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        // POST /v1/user/logout HTTP/1.1
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);

        // Content-type: application/x-www-form-urlencoded;charset=utf-8
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

//        int responseCode = httpURLConnection.getResponseCode();
    }

    // 토큰 정보 보기
    public boolean requestTokenInfo(String accessToken) throws Exception {

        String targetURL = "https://kapi.kakao.com/v1/user/access_token_info";
        URL url = new URL(targetURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        // GET /v1/user/access_token_info HTTP/1.1
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setDoOutput(true);

        httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + accessToken);

//        String messageBody = StreamUtils.copyToString(httpURLConnection.getInputStream(), StandardCharsets.UTF_8);

        return httpURLConnection.getResponseCode() == HttpStatus.OK.value();
    }

    private KakaoUserInfoDTO getKakaoUserInfo(String messageBody) throws JsonProcessingException {
        Map<String, Object> info = new ObjectMapper().readValue(messageBody, new TypeReference<Map<String, Object>>() {});

//        Long kakaoId = Long.parseLong(info.get("id").toString());
//        Map<String, String> properties = (Map<String, String>) info.get("properties");
//        String nickname = properties.get("nickname");
        Map<String, String> kakao_account = (Map<String, String>) info.get("kakao_account");
        String email = kakao_account.get("email");

//        return new KakaoUserInfoDTO(nickname, email);
        return new KakaoUserInfoDTO(email);
    }
}