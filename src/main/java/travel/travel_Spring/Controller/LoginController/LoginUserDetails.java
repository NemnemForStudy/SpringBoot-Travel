package travel.travel_Spring.Controller.LoginController;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import travel.travel_Spring.UserEntity.User;

import java.util.Collection;
import java.util.Collections;

// UserDetails -> SecurityConfig에서 제공해주는 인터페이스이다. 의존성에 이미 포함되어 있는 클래스임.
public class LoginUserDetails implements UserDetails {
    private final User user;

    public LoginUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    // getAuthorities() -> 사용자의 권한 정보 반환.
    // GrantedAuthority -> 권한 정보는 저걸 구현한 객체들의 컬렉션이어야 함.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자가 권한 가지지 않았음을 의미함. 만약 사용자에게 권한 부여하고 싶으면 이 리스트에 권한 객체 추가해야함.
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // 암호화된 비밀번호
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // isAccountNonExpired() 메소드는 계정이 만료되지 않았는지를 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // isAccountNonLocked() 메소드는 계정이 잠겨 있는지 여부를 반환
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // isCredentialsNonExpired() 메소드는 사용자의 인증 정보가 만료되지 않았는지 여부를 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
