package travel.travel_Spring.Controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import travel.travel_Spring.Service.UserService;

import java.util.Map;

@RestController
@RequestMapping("api/users")
public class UserApiController {
    @Autowired
    private UserService userService;

//    @GetMapping("/check-email")
//    public Map<String, String> checkEmail(@RequestParam String email) {
//        boolean exists = userService.checkEmailExists(email);
//    }
}
