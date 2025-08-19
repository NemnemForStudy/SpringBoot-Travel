package travel.travel_Spring.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import travel.travel_Spring.Controller.DTO.CardDto;
import travel.travel_Spring.Service.CardService;
import travel.travel_Spring.repository.CardRepository;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public Page<CardDto> findAll(Pageable pageable) {
        return cardRepository.findAll(pageable).map(card -> new CardDto(card));
    }
}
