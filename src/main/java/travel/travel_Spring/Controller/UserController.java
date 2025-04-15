package travel.travel_Spring.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import travel.travel_Spring.Controller.DTO.JoinMembershipDto;
import travel.travel_Spring.Service.UserService;
import travel.travel_Spring.UserEntity.User;

import java.time.LocalDate;

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
        // DTO에서 LocalDate로 변환
        LocalDate birth = joinMembershipDTO.getBirth();

        // User 객체 생성
        User user = new User(
                joinMembershipDTO.getEmail(),
                joinMembershipDTO.getPassword(),
                joinMembershipDTO.getNickname(),
                joinMembershipDTO.getPhoneNumber(),
                birth
        );

        // 사용자 저장
        boolean message = userService.saveUser(user);
        model.addAttribute("message", message);

        // 회원가입 성공 시 index 페이지로 리다이렉트
        if (!message) {
            return "joinMembership";
        } else {
            return "index";
        }
    }
}
