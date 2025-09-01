package travel.travel_Spring.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import travel.travel_Spring.Controller.DTO.CardDto;

// 서비스에서는 이러이러한 기능을 만들거다!
// ~~Impl 에서는 저 기능을 만든다!
public interface CardService {
    Page<CardDto> findAll(Pageable pageable);
}
