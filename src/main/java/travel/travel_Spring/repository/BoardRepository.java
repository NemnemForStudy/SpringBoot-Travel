package travel.travel_Spring.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import travel.travel_Spring.Entity.Board;

import java.util.Optional;


public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Board b SET b.author = :newAuthor WHERE b.author = :oldAuthor")
    void updateAuthorByAuthor(@Param("oldAuthor") String oldAuthor, @Param("newAuthor") String newAuthor);
}
