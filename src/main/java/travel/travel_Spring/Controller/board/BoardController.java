package travel.travel_Spring.Controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

        // 0부터 시작하는 페이지 번호와 페이지당 게시글 수 설정.
        Pageable pageable = PageRequest.of(page, 8); // 페이지당 8개

        // 서비스 메서드 호출해 Page<BoardDto> 객체 받음
        Page<BoardDto> resultPage = boardService.searchBoards(query, email, pageable);
        model.addAttribute("boards", resultPage.getContent()); // 페이지 게시글 목록
        model.addAttribute("currentPage", resultPage.getNumber());
        model.addAttribute("totalPages", resultPage.getTotalPages());
        model.addAttribute("query", query);

        return "search";
    }
}