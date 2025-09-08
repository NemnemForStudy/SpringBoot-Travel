package travel.travel_Spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardIdOrderByCreateTimeAsc(Long boardId);
    List<Comment> findAllByBoard(Board board);
    long countByBoardId(Long boardId);
}
