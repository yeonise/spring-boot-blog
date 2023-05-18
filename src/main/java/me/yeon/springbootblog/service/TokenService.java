package me.yeon.springbootblog.service;

import lombok.RequiredArgsConstructor;
import me.yeon.springbootblog.config.jwt.TokenProvider;
import me.yeon.springbootblog.domain.RefreshToken;
import me.yeon.springbootblog.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        if (!tokenProvider.validToken(refreshToken)) { // 토큰 유효성 검사에 실패하면 예외 발생
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
