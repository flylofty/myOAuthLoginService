package hello.login.domain.user;

import hello.login.web.dto.KakaoUserInfoDTO;

import javax.persistence.*;

@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Enumerated(EnumType.STRING)
    private UserRoleType role;

    //private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type")
    private UserLoginType loginType;

    public User() {}

    public User(KakaoUserInfoDTO userInfo) {
        this.email = userInfo.getEmail();
        this.role = UserRoleType.USER;
        this.loginType = UserLoginType.KAKAO;
        //this.nickname = userInfo.getKakaoNickname();
    }

    public Long getId() {
        return id;
    }
}
