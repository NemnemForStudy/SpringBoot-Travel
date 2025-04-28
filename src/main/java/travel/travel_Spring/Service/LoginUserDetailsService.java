package travel.travel_Spring.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import travel.travel_Spring.UserDetails.LoginUserDetails;
import travel.travel_Spring.UserEntity.User;
import travel.travel_Spring.repository.UserRepository;

// UserDetailsService는 Spring Security가 로그인 시 사용자 정보를 조회하는 데 사용하는 서비스 인터페이스
// 클래스의 주요 역할은 사용자의 이메일을 기반으로 데이터베이스에서 해당 사용자의 정보를 조회하고, 그 사용자 정보를 UserDetails 객체로 반환하는 것
@Service
public class LoginUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public LoginUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 로그인 시 이메일 기준으로 사용자 조회
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다 : " + email));

        return new LoginUserDetails(user);
    }
}
