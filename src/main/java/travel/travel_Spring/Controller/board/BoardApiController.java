package travel.travel_Spring.Controller.board;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.travel_Spring.Controller.DTO.BoardCountDto;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Service.BoardLikeService;
import travel.travel_Spring.Service.BoardService;
import travel.travel_Spring.Service.CardService;
import travel.travel_Spring.repository.BoardRepository;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@RestController // JSON 변환용(API)
@RequestMapping("/board")
@RequiredArgsConstructor  // 생성자 주입을 위한 어노테이션
public class BoardApiController {

    private final BoardService boardService;
    private final CardService cardService;
    private final BoardRepository boardRepository;
    private final BoardLikeService likeService;

    // 글 작성 (POST 요청)
    @PostMapping("/write")
    public ResponseEntity<?> createBoard(@RequestBody BoardDto boardDto) {
        boardService.createBoard(boardDto);

        // JSON 형태로 성공 응답 반환
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 게시글 리스트 조회
    @GetMapping("/travelDestination")
    public ResponseEntity<BoardCountDto> getTravelBoards() {
        List<BoardDto> boards = boardService.getAllBoards();

        // 게시글 페이지네이션
        long totalCount = boards.size();

        BoardCountDto response = new BoardCountDto(boards, totalCount);
        return ResponseEntity.ok(response);
    }

//    // REST API : JSON 데이터 변환
//    @GetMapping("/api/board/{id}")
//    public ResponseEntity<BoardDto> getBoardDetail(@PathVariable Long id) {
//        BoardDto boardDto = boardService.getBoardById(id);
//        return ResponseEntity.ok(boardDto);
//    }

    // 게시글의 좋아요 추가/삭제하고, 최신 좋아요 수 클라이언트에 전달.
    // boardDetailFunction.js Post
    @PostMapping("/api/board/{id}/like")
    public ResponseEntity<Map<String, Object>> likeBoard(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        boolean liked = request.get("liked");
        int updatedCount = boardService.updateLike(id, liked);
        return ResponseEntity.ok(Map.of("success", true, "likeCount", updatedCount));
    }

    // 특정 게시글의 상세 정보, 현재 사용자가 좋아요 눌렀는지 상태 함께 제공.
    // boardDetailFunction.js Get
    @GetMapping("/api/board/{boardId}")
    public ResponseEntity<BoardDto> getBoard(@PathVariable Long boardId, Principal principal) {
        Board board = boardService.findById(boardId);
        boolean likedByUser = boardService.hasUserLiked(boardId, principal.getName());

        BoardDto boardDto = new BoardDto();
        boardDto.setId(board.getId());
        boardDto.setTitle(board.getTitle());
        boardDto.setContent(board.getContent());
        boardDto.setLikeCount(board.getLikeCount());
        boardDto.setLikedByCurrentUser(likedByUser);

        return ResponseEntity.ok(boardDto);
    }


//    // 게시글 디테일 화면
//    @GetMapping("/boardDetailForm/{id}")
//    // GET 요청은 보통 URL 경로(@PathVariable)나 쿼리 파라미터(@RequestParam)로 데이터 전달
//    public ResponseEntity<BoardDto> detailBoard(@PathVariable Long id) {
//        BoardDto boardDto = boardService.getBoardById(id);
//
//        return ResponseEntity.ok(boardDto);
//    }
}