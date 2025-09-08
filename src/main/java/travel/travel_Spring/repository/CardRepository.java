package travel.travel_Spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import travel.travel_Spring.Entity.Card;

import java.util.List;

// JpaRepository 덕분에 기본적인 findAll, save 같은 건 이미 다 만들어져 있다.
public interface CardRepository extends JpaRepository<Card, Long> {
        @Query(value = """
    SELECT b.id AS boardId,
           b.title AS title,
           b.content AS content,
           b.like_count AS likeCount,
           (SELECT p.picture_url 
            FROM board_picture p 
            WHERE p.board_id = b.id 
            ORDER BY p.id ASC 
            LIMIT 1) AS picture,
           COUNT(c.comment_id) AS commentCount,
           (b.like_count + COUNT(c.comment_id)) AS score
    FROM board b
    LEFT JOIN comment_table c ON b.id = c.board_id
    GROUP BY b.id, b.title, b.like_count
    ORDER BY score DESC
    """, nativeQuery = true)
        List<Object[]> findAllBoards();
}
