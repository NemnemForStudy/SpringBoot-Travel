package travel.travel_Spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel_Spring.Entity.Board;


public interface BoardRepository extends JpaRepository<Board, Long> {
}
