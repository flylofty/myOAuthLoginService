package hello.login.service;

import hello.login.domain.token.Token;
import hello.login.domain.user.User;
import hello.login.repository.UserRepository;
import hello.login.web.dto.KakaoTokenDTO;
import hello.login.web.dto.KakaoUserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoUserService {

    private final UserRepository userRepository;

    // 로그인
    @Transactional
    public void login(KakaoUserInfoDTO userInfo, KakaoTokenDTO token) {

        //사용자 확인 후
        // A. 신규 사용자인 경우
        // => 사용자 및 토큰 정보를 저장 (회원가입)
        // B. 이미 가입된 경우
        // => 토큰 정보 갱신 (로그인)

        Optional<User> optionalUser = findUserByEmail(userInfo);

        // 신규 사용자
        // => 사용자 및 토큰 정보를 저장
        if (optionalUser.isEmpty()) {
            join(userInfo, token);
            //return join(userInfo, token);
        }

        // 재방문 사용자, 로그인
        // => 토큰 정보 갱신
        optionalUser.ifPresent(user -> userRepository.updateUserToken(user.getId(), token));
        //return optionalUser.get().getId();
    }

    // 마이페이지

    // 로그아웃
    @Transactional
    public void logout(KakaoUserInfoDTO userInfo) {

        Optional<User> optionalUser = findUserByEmail(userInfo);
        userRepository.updateUserToken(optionalUser.get().getId(), new KakaoTokenDTO());
    }

    // 회원가입
    private void join(KakaoUserInfoDTO userInfo, KakaoTokenDTO token) {

        userRepository.saveUser(new User(userInfo));
        Optional<User> optionalUser = findUserByEmail(userInfo);

         // 임시 처리
         // 확실한 처리 궁리해봅시다!
        if (optionalUser.isEmpty()) {
            //return 0L;
        }

        userRepository.saveUserToken(new Token(optionalUser.get().getId(), token));
        //return optionalUser.get().getId();
    }

    private Optional<User> findUserByEmail(KakaoUserInfoDTO userInfo) {

        Optional<User> optionalUser = userRepository.findByEmail(userInfo);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        return optionalUser;
    }
}