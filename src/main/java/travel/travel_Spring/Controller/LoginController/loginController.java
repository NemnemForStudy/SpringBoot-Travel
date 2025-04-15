package travel.travel_Spring.Controller.LoginController;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class loginController {

    // 로그인 페이지
    @GetMapping("/loginView")
    public String loginView(Model model) {
        return "loginView";
    }

    // 로그인 성공 후 이동 페이지
    @GetMapping("/loginAfterIndexView")
    public String loginAfterIndexView(Model model) {
        return "redirect:/";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

}
