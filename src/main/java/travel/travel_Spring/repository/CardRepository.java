package travel.travel_Spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel_Spring.Entity.Card;

// JpaRepository 덕분에 기본적인 findAll, save 같은 건 이미 다 만들어져 있다.
public interface CardRepository extends JpaRepository<Card, Long> {
}
