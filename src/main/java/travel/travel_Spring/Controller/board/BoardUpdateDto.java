package travel.travel_Spring.Controller.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

// 수정할 값만 담는 용도
public class BoardUpdateDto {
    private String title;
    private String content;
}
