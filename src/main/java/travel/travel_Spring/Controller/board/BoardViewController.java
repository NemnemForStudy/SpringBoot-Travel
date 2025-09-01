package travel.travel_Spring.Controller.board;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import travel.travel_Spring.Controller.DTO.BoardCountDto;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Service.BoardService;

import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardViewController {

    private BoardService boardService;

    // boardService 같은게 null이 뜬다면
    // 생성자 주입
    public BoardViewController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 새 글, 수정 글 둘 다 같은 HTML 사용
    @GetMapping("/update")
    public String writeForm(@RequestParam(required = false) Long boardId, Model model) {
        if(boardId != null) {
            Board board = boardService.findById(boardId);
            model.addAttribute("board", board);
        }
        return "write";
    }
}
