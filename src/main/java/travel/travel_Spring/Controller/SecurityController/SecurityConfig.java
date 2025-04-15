package travel.travel_Spring.Controller.SecurityController;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            // 로그인 여부와 상관없이 도메인에 진입할 수 있게 지정해줘야 함.
            // Image를 지정해줘야 로그인하지 않을 때도 이미지가 나오게 된다.
            .antMatchers("/", "/loginView", "/css/**", "/js/**", "/index", "/joinMembership", "/Image/**").permitAll() // 이 부분 중요
            .anyRequest().authenticated()
        .and()
            .formLogin()
            .loginPage("/loginView")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/", true)
            .failureUrl("/loginView?error=true")
            .permitAll() // 여기도 authorizeRequests() 안에 있어야 함
        .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID");

        return http.build();
    }
}