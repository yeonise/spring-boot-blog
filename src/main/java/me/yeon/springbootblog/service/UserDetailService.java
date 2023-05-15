package me.yeon.springbootblog.service;

import lombok.RequiredArgsConstructor;
import me.yeon.springbootblog.domain.User;
import me.yeon.springbootblog.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService { // 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(final String email) { // 사용자 이름(email)으로 사용자의 정보를 가져오는 메서드
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email));
    }
}
