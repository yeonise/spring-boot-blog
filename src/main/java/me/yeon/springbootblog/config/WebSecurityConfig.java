package me.yeon.springbootblog.config;

import lombok.RequiredArgsConstructor;
import me.yeon.springbootblog.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailService userService;

    // 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer configure() { // 스프링 시큐리티의 모든 기능을 사용하지 않게 설정 / 즉, 인증과 인가 서비스를 모든 곳에 적용하지 않는다
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console()) // h2-console 하위 url을 대상으로
                .requestMatchers("/static/**"); // static 하위 경로에 있는 리소스를 대상으로
    }

    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests()
                .requestMatchers("/login", "/signup", "/user").permitAll() // 특정 경로에 대한 액세스 설정 // 누구나 접근 가능
                    .anyRequest().authenticated() // 위에서 설정한 url 이외의 요청에 대한 설정 // 인가는 필요하지 않지만 인증해야 접근 가능
                    .and()
                .formLogin() // 폼 기반 로그인 설정
                    .loginPage("/login") // 로그인 페이지 경로 설정
                    .defaultSuccessUrl("/articles") // 로그인 성공 시 이동할 경로 설정 // 유의 * if they have not visited a secured page prior to authenticating
                    .and()
                .logout() // 로그아웃 설정
                    .logoutSuccessUrl("/login") // 로그아웃 성공 시 이동할 경로 설정
                    .invalidateHttpSession(true) // 로그아웃 이후 세션 전체 삭제 여부 설정
                    .and()
                .csrf().disable() // csrf 비활성화
                .build();
    }

    // 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService) // 사용자 정보를 가져올 서비스를 설정 : 이때 설정하는 서비스 클래스는 반드시 UserDetailService를 상속받은 클래스여야 한다
                .passwordEncoder(bCryptPasswordEncoder) // 비밀번호를 암호화하기 위한 인코더 설정
                .and()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { // 패스워드 인코더로 사용할 빈 등록
        return new BCryptPasswordEncoder();
    }
}
