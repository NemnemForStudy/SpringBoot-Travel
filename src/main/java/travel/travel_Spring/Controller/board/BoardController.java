package travel.travel_Spring.Controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import travel.travel_Spring.Controller.DTO.BoardDto;
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

    @GetMapping("/search")
    public String searchBoards(@RequestParam String query,
                               @RequestParam(defaultValue = "0") int page,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        String email = userDetails.getUsername();

        Pageable pageable = PageRequest.of(page, 8);
        List<BoardDto> results = boardService.searchBoards(query, email);
        model.addAttribute("boards", results);
        return "search";
    }
}