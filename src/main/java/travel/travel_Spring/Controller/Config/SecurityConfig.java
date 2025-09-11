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
            // 로그인 여부와 상관없이 도메인에 진입할 수 있게 지정해줘야 함.
            // Image를 지정해줘야 로그인하지 않을 때도 이미지가 나오게 된다.
            // board를 추가해서 postman 할 수 있게 해줌.
            .antMatchers("/", "/loginView", "/css/**", "/js/**", "/index", "/joinMembership", "/Image/**", "/api/**", "/board/**", "/uploads/**", "/api/naver/direction").permitAll() // 이 부분 중요
            .anyRequest().authenticated()
        .and()
            .formLogin()
            .loginPage("/loginView")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/", true)
            .failureHandler(loginFailureHandler)
            .failureUrl("/loginView?error=true")
            .permitAll() // 여기도 authorizeRequests() 안에 있어야 함
        .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID");// 쿠키 삭제

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
        if(authentication != null && authentication.getPrincipal() instanceof LoginUserDetails) {
            LoginUserDetails loginUserDetails = (LoginUserDetails) authentication.getPrincipal();
            return loginUserDetails.getNickname();
        }
        return null;
    }

    // 로그인 된 사용자 이메일 가져오기
    public static String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUserDetails loginUserDetails = (LoginUserDetails) authentication.getPrincipal();
        return loginUserDetails.getEmail();
    }
}