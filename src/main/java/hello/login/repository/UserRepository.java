package hello.login.repository;

import hello.login.domain.token.Token;
import hello.login.domain.user.User;
import hello.login.web.dto.KakaoTokenDTO;
import hello.login.web.dto.KakaoUserInfoDTO;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

// @Repository 은 스프링을 사용할 때 사용하는 애노테이션임
// 컴포넌트 스캔에 의해 자동으로 스프링 빈으로 등록됨
@Repository
public class UserRepository {

    /**
     * EntityManager 를 통하여 회원과 관련한
     * 데이터베이스 접근 로직을 만듦
     */

    // Spring Data JPA 가 아닌 순수 JPA 를 사용함
    // 스프링이 EntityManager 를 만들어서 주입해줌
    @PersistenceContext
    private EntityManager em;

    // 사용자 정보 저장
    public void saveUser(User user) {
        em.persist(user);
    }

    // email 을 통한 사용자 정보 조회하고 해당 정보를 전달
    public Optional<User> findByEmail(KakaoUserInfoDTO userInfo) {

        // email 을 통하여 사용자 정보를 조회
        List<User> findUser = em.createQuery("select u from User u " +
                                                     "where u.email = :userEmail", User.class)
                .setParameter("userEmail", userInfo.getEmail())
                .getResultList();

//        if (findUser.isEmpty()) {
//            return Optional.empty();
//        }
//        return Optional.ofNullable(findUser.get(0));

        return findUser.stream().findAny();
    }

    // 사용자 토큰 정보 저장
    public void saveUserToken(Token token) {
        em.persist(token);
    }

    // 사용자 토큰 정보 갱신
    public void updateUserToken(Long id, KakaoTokenDTO newToken) {
        Token oldToken = em.find(Token.class, id);
        oldToken.update(newToken);
    }

    // 사용자 토큰 제거
    public void deleteUserToken(Long id) {
        em.remove(em.find(Token.class, id));
    }
}