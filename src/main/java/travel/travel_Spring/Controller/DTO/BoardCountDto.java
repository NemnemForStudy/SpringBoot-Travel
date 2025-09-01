package travel.travel_Spring.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.travel_Spring.Entity.Board;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BoardCountDto {
    private List<BoardDto> boards;
    private long totalCount;

    public BoardCountDto(List<BoardDto> boards, long totalCount) {
        this.boards = boards;
        this.totalCount = totalCount;
    }
}
