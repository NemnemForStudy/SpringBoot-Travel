package travel.travel_Spring.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CommentRequestDto {
    private String content; // 클라이언트에서 입력하는 댓글 내용만
}
