package travel.travel_Spring.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import travel.travel_Spring.Controller.BCryptEncryptor.Encryptor;
import travel.travel_Spring.Controller.DTO.JoinMembershipDto;
import travel.travel_Spring.UserEntity.User;
import travel.travel_Spring.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

// 자동으로 빈으로 등록 해 Autowired로 주입할 수 있게 된다.
@Service
public class UserService {


    // CRUD 작업을 제공하는 리포지토리.
    private UserRepository userRepository;
    private Encryptor encryptor;
    @Autowired
    public UserService(Encryptor encryptor, UserRepository userRepository) {
        this.encryptor = encryptor;
        this.userRepository = userRepository;
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

    public boolean checkPassword(String rawPassword, String storedPassword) {
        return encryptor.isMatch(rawPassword, storedPassword);
    }

    public Optional<User> getEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 비밀번호가 일치하는지 확인
            return checkPassword(password, user.getPassword());
        }
        return false; // 이메일이 존재하지 않으면 로그인 실패
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

        if(userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 암호화함.
        String encodedPassword = encryptor.encrypt(password);

        User user = new User(email, encodedPassword, nickname, phoneNumber, birthDate);
        userRepository.save(user);
    }
}
