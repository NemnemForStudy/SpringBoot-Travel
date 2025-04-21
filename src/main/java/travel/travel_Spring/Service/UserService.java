package travel.travel_Spring.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import travel.travel_Spring.Controller.DTO.JoinMembershipDto;
import travel.travel_Spring.UserEntity.User;
import travel.travel_Spring.repository.UserRepository;

import java.time.LocalDate;

// 자동으로 빈으로 등록 해 Autowired로 주입할 수 있게 된다.
@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;  // PasswordEncoder 주입

    // CRUD 작업을 제공하는 리포지토리.
    private UserRepository userRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // 사용자 지정
    public boolean saveUser(User user) {

        if(userRepository.existsByEmail(user.getEmail())) {
            return false;
        } else {
            userRepository.save(user);
            return true;
        }
    }

    public boolean isNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void signup(JoinMembershipDto dto) {
        String email = dto.getEmail();
        String nickname = dto.getNickname();
        String password = dto.getPassword();
        String phoneNumber = dto.getPhoneNumber();
        LocalDate birthDate = dto.getBirth();

        if (email == null || nickname == null || password == null || phoneNumber == null || birthDate == null) {
            throw new IllegalArgumentException("필수 값이 누락되었습니다.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 암호화함.
        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(email, encodedPassword, nickname, phoneNumber, birthDate);
        userRepository.save(user);
    }
}
