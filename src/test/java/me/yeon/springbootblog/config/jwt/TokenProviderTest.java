package me.yeon.springbootblog.config.jwt;

import io.jsonwebtoken.Jwts;
import me.yeon.springbootblog.domain.User;
import me.yeon.springbootblog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken: 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다")
    @Test
    void generateToken() {
        User testUser = userRepository.save(User.builder() // 토큰에 유저 정보를 추가하기 위한 테스트 유저 셍성
                .email("user@gamil.com")
                .password("test")
                .build());

        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14)); // tokenProvider의 generateToken 메서드를 호출해 토큰 생성

        Long userId = Jwts.parser() // jjwt 라이브러리를 사용해 토큰을 복호화
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId()); // 클레임으로 넣어둔 id 값이 테스트 유저 id와 동일한지 확인
    }

    @DisplayName("validToken: 만료된 토큰인 때에 유효성 검증에 실패한다")
    @Test
    void invalidToken() {
        String token = JwtFactory.builder() // 이미 만료된 토큰 생성
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        boolean result = tokenProvider.validToken(token); // 유효한 토큰인지 검증한 뒤 결과 값을 반환

        assertThat(result).isFalse(); // 반환 값이 false인지 확인
    }

    @DisplayName("validToken: 유효한 토큰인 때에 유효성 검증에 성공한다")
    @Test
    void validToken() {
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties); // 기본 값을 사용하여 만료되지 않은 토큰 생성

        boolean result = tokenProvider.validToken(token); // 유효한 토큰인지 검증한 뒤 결과 값을 반환

        assertThat(result).isTrue(); // 반환 값이 true인지 확인
    }

    @DisplayName("getAuthentication: 토큰 기반으로 인증 정보를 가져올 수 있다")
    @Test
    void getAuthentication() {
        String userEmail = "user@email.com";
        String token = JwtFactory.builder() // jjwt 라이브러리를 사용하여 토큰 생성 / 토큰의 제목인 subject는 "user@email.com"으로 설정
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        Authentication authentication = tokenProvider.getAuthentication(token); // getAuthentication 메서드를 호출하여 인증 객체를 반환

        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail); // 반환받은 인증 객체의 유저 이름이 설정한 subject와 일치하는지 확인
    }

    @DisplayName("getUserId: 토큰으로 유저 ID를 가져올 수 있다")
    @Test
    void getUserId() {
        Long userId = 1L;
        String token = JwtFactory.builder() // 토큰 생성시 클레임을 추가 / key : id / value : 1
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        Long userIdByToken = tokenProvider.getUserId(token); // getUserId 메서드를 호출하여 유저 ID를 반환

        assertThat(userIdByToken).isEqualTo(userId); // 반환받은 유저 ID가 설정한 유저 ID 값인 1과 동일한지 확인
    }
}
