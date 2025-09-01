package travel.travel_Spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.travel_Spring.Entity.BoardPicture;

public interface BoardPictureRepository extends JpaRepository<BoardPicture, Long> {
}
