package travel.travel_Spring.Controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Service.BoardService;

import java.util.Map;


@RestController
@RequestMapping("/board")
@RequiredArgsConstructor  // 생성자 주입을 위한 어노테이션
public class BoardApiController {

    private final BoardService boardService;

    // 글 작성 (POST 요청)
    @PostMapping("/write")
    public ResponseEntity<?> createBoard(@RequestBody BoardDto boardDto) {
        Map<String, Object> response = boardService.createBoard(boardDto);

        // JSON 형태로 성공 응답 반환
        return ResponseEntity.ok(response);
    }
}