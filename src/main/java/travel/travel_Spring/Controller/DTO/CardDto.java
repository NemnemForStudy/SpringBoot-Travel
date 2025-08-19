package travel.travel_Spring.Controller.DTO;

import lombok.Getter;
import travel.travel_Spring.Entity.Card;

// 엔터티를 그대로 보내지 않고 Dto를 사용해서 필요한 정보만 넘김.
@Getter
public class CardDto {
    private Long id;
    private String title;
    private String content;
    private String imgUrl;

    // Entity -> DTO 반환
    public CardDto(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.content = card.getContent();
        this.imgUrl = card.getImgUrl();
    }
}
