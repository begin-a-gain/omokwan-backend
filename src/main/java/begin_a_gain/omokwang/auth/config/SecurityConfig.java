package begin_a_gain.omokwang.auth.config;

import static org.springframework.security.config.Customizer.withDefaults;

import begin_a_gain.omokwang.auth.enums.UserRole;
import begin_a_gain.omokwang.auth.filter.JwtFilter;
import begin_a_gain.omokwang.auth.service.JwtTokenService;
import begin_a_gain.omokwang.exception.ExceptionHandlerFilter;
import begin_a_gain.omokwang.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration // spring 설정 클래스
public class SecurityConfig {
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
        return http.cors(withDefaults())
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/login/**", "/token/refresh").permitAll()
                        .requestMatchers("/user/**").hasAuthority(UserRole.USER.getRole())
                        .anyRequest().authenticated())
                .sessionManagement(
                        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 사용하지 않음
                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.disable()) // 로그인 폼 미사용
                .httpBasic(
                        httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer.disable()) // http basic 미사용
                .addFilterBefore(new JwtFilter(jwtTokenService, userService),
                        UsernamePasswordAuthenticationFilter.class) // JWT Filter 추가
                .addFilterBefore(new ExceptionHandlerFilter(),
                        JwtFilter.class) // Security Filter 에서 CustomException 사용하기 위해 추가
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 아래 url은 filter 에서 제외
        return web ->
                web.ignoring()
                        .requestMatchers("/login/**", "/token/refresh");
    }
}
