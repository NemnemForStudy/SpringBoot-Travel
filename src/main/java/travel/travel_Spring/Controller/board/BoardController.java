package travel.travel_Spring.Controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import travel.travel_Spring.Service.BoardService;

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
}