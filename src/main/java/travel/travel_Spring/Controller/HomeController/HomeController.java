package travel.travel_Spring.Controller.HomeController;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("images", Arrays.asList("static/Image/사진1.png", "static/Image/사진2.png", "static/Image/사진3.png"));
        return "index";
    }

    @GetMapping("/myPage")
    public String myPage(Model model) {
        return "myPage";
    }
}
