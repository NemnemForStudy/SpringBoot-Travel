package travel.travel_Spring.Controller.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import travel.travel_Spring.Handler.LoginFailureHandler;
import travel.travel_Spring.Service.LoginUserDetailsService;

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
            .antMatchers("/", "/loginView", "/css/**", "/js/**", "/index", "/joinMembership", "/Image/**", "/api/**").permitAll() // 이 부분 중요
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
}