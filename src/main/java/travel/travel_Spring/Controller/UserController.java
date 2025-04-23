package travel.travel_Spring.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import travel.travel_Spring.Controller.DTO.JoinMembershipDto;
import travel.travel_Spring.Service.UserService;
import travel.travel_Spring.UserEntity.User;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 처리
    @PostMapping("/joinMembership")
    public String joinMembership(@ModelAttribute JoinMembershipDto joinMembershipDTO, Model model) {
        try {
            LocalDate birth = joinMembershipDTO.getBirth();

            User user = new User(
                    joinMembershipDTO.getEmail(),
                    joinMembershipDTO.getPassword(),
                    joinMembershipDTO.getNickname(),
                    joinMembershipDTO.getPhoneNumber(),
                    birth
            );

            boolean message = userService.saveUser(user);
            model.addAttribute("message", message);

            if (!message) {
                model.addAttribute("errorMessage", "회원가입에 실패했습니다. 다시 시도해주세요.");
                return "joinMembership"; // 에러 시에도 그대로 stay
            } else {
                return "index";
            }

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage()); // 예외 메시지를 model에 담음
            return "joinMembership"; // 다시 그 페이지로
        }
    }

    @GetMapping("/mypage")
    public String getUserDataValue(Model model, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOptional = userService.getEmail(email);

        if(userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
        } else {
            model.addAttribute("error", "사용자를 찾을 수 없습니다.");
        }

        return "mypage";
    }
}