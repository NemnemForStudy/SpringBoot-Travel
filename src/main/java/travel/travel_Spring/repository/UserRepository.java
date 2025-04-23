package travel.travel_Spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel_Spring.UserEntity.User;

import java.util.Optional;

// Spring Data JPA를 사용해 DB와 상호작용하기 위한 UserRepository를 정의. JpaRepository를 상속받음으로써
// 기본적인 CRUD 작업 자동 제공함.
public interface UserRepository extends JpaRepository<User, Long> {

    // 변경
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByPhoneNumber(String phoneNumber);

}
