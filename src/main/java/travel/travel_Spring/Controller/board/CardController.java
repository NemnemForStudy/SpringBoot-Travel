package travel.travel_Spring.Controller.board;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.travel_Spring.Impl.CardServiceImpl;

import java.util.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardServiceImpl cardService;

    public CardController(CardServiceImpl cardService) {
        this.cardService = cardService;
    }

    // 상위 3개 게시물 조회
    @GetMapping("/top3")
    public List<Map<String, Object>> getTop3Boards() {
        List<Object[]> results = cardService.getTop3Boards();
        List<Map<String, Object>> response = new ArrayList<>();

        // id, content, likeCount, picture, commentCount, like + comment
        for(Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("boardId", row[0]);
            map.put("title", row[1]);
            map.put("content", row[2]);
            map.put("likeCount", row[3]);
            map.put("picture", row[4]);
            map.put("commentCount", row[5]);
            map.put("score", row[6]);
            response.add(map);
        }

        return response;
    }
}
