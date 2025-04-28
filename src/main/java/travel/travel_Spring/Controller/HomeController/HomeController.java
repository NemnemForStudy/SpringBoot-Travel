package travel.travel_Spring.Controller.HomeController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Service.BoardService;

import java.util.Arrays;

@Controller
public class HomeController {

    private final BoardService boardService;

    public HomeController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("images", Arrays.asList("static/Image/사진1.png", "static/Image/사진2.png", "static/Image/사진3.png"));
        return "index";
    }

    @GetMapping("/travelDestination")
    public String travelDestination() {
        return "travelDestination";
    }

    @GetMapping("/write")
    public String showWritePage(Model model) {
        model.addAttribute("board", new BoardDto());  // DTO를 바로 넘기도록 변경
        return "write";
    }

    @PostMapping("/write")
    public String createBoard(@ModelAttribute BoardDto dto) {
        boardService.createBoard(dto);  // 서비스에서 처리하도록 변경
        return "redirect:/travelDestination";
    }
}
