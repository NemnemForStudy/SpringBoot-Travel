package travel.travel_Spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel_Spring.UserEntity.User;

// Spring Data JPA를 사용해 DB와 상호작용하기 위한 UserRepository를 정의. JpaRepository를 상속받음으로써
// 기본적인 CRUD 작업 자동 제공함.
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);

    boolean existsByEmail(String email);
    // 추가 쿼리 필요 시 작성
}
