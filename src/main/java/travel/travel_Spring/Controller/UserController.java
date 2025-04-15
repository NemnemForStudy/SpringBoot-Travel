package travel.travel_Spring.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import travel.travel_Spring.Service.UserService;
import travel.travel_Spring.UserEntity.User;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/joinMembership")
    public String joinMembership() {
        return "joinMembership";
    }

    // 회원가입 처리
    @PostMapping("/joinMembership")
    public String joinMembership(@RequestParam("username") String email, @RequestParam("password") String password, Model model) {

        // User 객체 생성
        User user = new User(email, password);
        boolean message = userService.saveUser(user);
        model.addAttribute("message", message);

        // message가 false 면 true로 변경되어 joinMembership으로, 아니면 redirect 함.
        if(!message) {
            return "joinMembership";
        } else {
            return "index";
        }
    }
}
