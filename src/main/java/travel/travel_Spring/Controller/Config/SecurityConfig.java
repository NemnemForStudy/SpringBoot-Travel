package travel.travel_Spring.Controller.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Handler.LoginFailureHandler;
import travel.travel_Spring.Service.LoginUserDetailsService;
import travel.travel_Spring.UserDetails.LoginUserDetails;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private LoginUserDetailsService userDetailsService;

    // PasswordEncoder 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SecurityFilterChain 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/",
                        "/loginView",
                        "/css/**",
                        "/js/**",
                        "/index",
                        "/joinMembership",
                        "/Image/**",
                        "/uploads/**",
                        "/api/**",                           // 먼저 배치
                        "/board/boardDetailForm/**",         // 상세페이지
                        "/board/api/**",                     // board API
                        "/board/**",                         // 마지막에 배치
                        "/api/naver/direction"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/loginView")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureHandler(loginFailureHandler)
                .failureUrl("/loginView?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    // 사용자가 로그인 후, 정보를 활용해 필요 작업 할 수 있게 설정할 때 사용.
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    // 로그인 된 사용자 닉네임 가져오는 방법
    public static String getCurrentNickname() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())
                && authentication.getPrincipal() instanceof LoginUserDetails) {
            LoginUserDetails loginUserDetails = (LoginUserDetails) authentication.getPrincipal();
            return loginUserDetails.getNickname();
        }
        return null;
    }

    // 로그인 된 사용자 이메일 가져오기
    public static String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())
                && authentication.getPrincipal() instanceof LoginUserDetails) {
            LoginUserDetails loginUserDetails = (LoginUserDetails) authentication.getPrincipal();
            return loginUserDetails.getEmail();
        }
        return null;
    }
}