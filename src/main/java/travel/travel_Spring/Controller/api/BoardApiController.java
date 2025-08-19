package travel.travel_Spring.Controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Controller.DTO.CardDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Service.BoardService;
import travel.travel_Spring.Service.CardService;
import travel.travel_Spring.repository.BoardRepository;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/board")
@RequiredArgsConstructor  // 생성자 주입을 위한 어노테이션
public class BoardApiController {

    private final BoardService boardService;
    private final CardService cardService;
    private final BoardRepository boardRepository;

    // 글 작성 (POST 요청)
    @PostMapping("/write")
    public ResponseEntity<?> createBoard(@RequestBody BoardDto boardDto) {
        boardService.createBoard(boardDto);

        // JSON 형태로 성공 응답 반환
        return ResponseEntity.ok(Map.of("success", true));
    }
//
//    // 페이지네이션
//    @GetMapping("/travelDestination")
//    // Card 데이터가져올 때 PageRequest를 사용.
//    public String travelDestination(Model model, @PageableDefault(size=9) Pageable pageable) {
//        Page<CardDto> cardPage = cardService.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
//
//        model.addAttribute("page", cardPage); // 현재 페이지 카드들
//        model.addAttribute("cards", cardPage.getContent()); // 페이지 정보 전체
//        return "travelDestination"; // 타임리프 페이지 이름.
//    }

    // 게시글 리스트 조회
    @GetMapping("/travelDestination")
    public ResponseEntity<List<BoardDto>> getTravelBoards() {
        List<BoardDto> boards = boardService.getAllBoards();
        return ResponseEntity.ok(boards);
    }
}