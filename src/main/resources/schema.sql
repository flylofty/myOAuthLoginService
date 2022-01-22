drop table if exists token;
drop table if exists user;

CREATE TABLE user (
    id        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    email     VARCHAR(255) NOT NULL,
    role      VARCHAR(255) NOT NULL,
    login_type VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX nix_user_email on user(email);

CREATE TABLE token (
    id                 BIGINT UNSIGNED NOT NULL,
    refresh_token      VARCHAR(255),
    refresh_expires_in INTEGER UNSIGNED,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES user (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 아래는 원래 계획했던 테이블
--CREATE TABLE user (
--    id        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
--    password  VARCHAR(255),
--    email     VARCHAR(255) NOT NULL,
--    role      VARCHAR(255) NOT NULL,
--    nickname  VARCHAR(255),
--    join_type VARCHAR(255),
--    PRIMARY KEY (id)
--) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
--CREATE INDEX nix_user_email on user(email);
--
--CREATE TABLE token (
--    id                 BIGINT UNSIGNED NOT NULL,
--    access_token       VARCHAR(255),
--    refresh_token      VARCHAR(255),
--    expires_in         INTEGER UNSIGNED,
--    refresh_expires_in INTEGER UNSIGNED,
--    PRIMARY KEY (id),
--    FOREIGN KEY (id) REFERENCES user (id) ON DELETE CASCADE
--) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--CREATE INDEX nix_token_id on token(id);

-- 바로 위 인덱스는 필요한지 모르겠음
-- 기본으로 PK에 대하여 인덱스가 생성되므로 있을 필요가 없을 것 같음