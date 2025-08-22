package travel.travel_Spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel_Spring.Entity.BoardLike;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    boolean existsByBoardIdAndUserEmail(Long boardId, String email);
    void deleteByBoardIdAndUserUsername(Long boardId, String username);
    long countByBoardId(Long boardId);
}
