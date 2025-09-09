package travel.travel_Spring.Controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Service.BoardService;

import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    
    private final BoardService boardService;

    // HTML 페이지 변환, 게시글 디테일 페이지 이동
    @GetMapping("/boardDetailForm/{id}")
    public String boardDetailFormPage(@PathVariable Long id, Model model) {
        model.addAttribute("boardId", id);
        return "boardDetailForm";
    }

    // 전체 게시글 리스트
    @GetMapping("/search")
    public String boardListPage(@RequestParam("query") String query, Model model) {
        List<Board> boards = boardService.searchByTitle(query);
        List<BoardDto> boardDtos = boards.stream()
                        .map(BoardDto::new)
                        .toList();

        model.addAttribute("boards", boardDtos);
        return "search";
    }
}