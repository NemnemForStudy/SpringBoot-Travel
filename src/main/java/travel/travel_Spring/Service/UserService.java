package travel.travel_Spring.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import travel.travel_Spring.UserEntity.User;
import travel.travel_Spring.repository.UserRepository;

// 자동으로 빈으로 등록 해 Autowired로 주입할 수 있게 된다.
@Service
public class UserService {

    // 의존성 주입을 위해 사용된다. UserRepository를 자동으로 주입. 인스턴스 자동 할당. 04-14 요즘엔 잘 안쓴다 함.
//    @Autowired

    // CRUD 작업을 제공하는 리포지토리.
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
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
}
