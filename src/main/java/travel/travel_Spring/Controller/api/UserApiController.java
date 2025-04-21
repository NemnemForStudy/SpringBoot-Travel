package travel.travel_Spring.Controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import travel.travel_Spring.Controller.DTO.*;
import travel.travel_Spring.Service.EmailService;
import travel.travel_Spring.Service.UserService;
import travel.travel_Spring.UserEntity.User;
import travel.travel_Spring.repository.UserRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserApiController {
    @Autowired
    private UserService userService;
    private EmailService emailService;
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    public UserApiController(EmailService emailService) {
        this.emailService = emailService;
    }

    // 이메일 중복을 확인한다.
    // 사용가능하면 UserController -> true가 넘어와서 이 api를 사용.
    @GetMapping("/joinMembership")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = emailService.checkEmailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", !exists);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkPhoneNumber")
    // @RequestBody -> 클라이언트가 JSON 데이터를 보내고 서버가 DTO로 받으려면 POST, PUT 요청으로 사용한다.
    // GET도 사용 가능하다. 단점은 URL이 길어질 수 있다.
    public Map<String, Boolean> checkPhoneNumber(@RequestBody PhoneNumberDto dto) {
        Map<String, Boolean> response = new HashMap<>();

        boolean isValid = dto.isValidPhoneNumber();
        response.put("valid", isValid);

        return response;
    }

    @PostMapping("/passwordCheck")
    public ResponseEntity<Map<String, Boolean>> passwordCheck(@RequestBody PasswordLengthDto dto) {
        Map<String, Boolean> response = new HashMap<>();
        boolean passwordCheck = dto.getPassword().length() > 8;
        response.put("passwordCheck", passwordCheck);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/passwordInput")
    public Map<String, Boolean> checkPasswordLength(@RequestBody PasswordLengthDto dto) {
        Map<String, Boolean> response = new HashMap<>();
        boolean valid = dto.isValid();
        response.put("valid", valid);
        return response;
    }

    @PostMapping("/nicknameInput")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestBody NicknameDto dto) {
        Map<String, Boolean> response = new HashMap<>();
        boolean nicknameExists = userService.isNicknameExists(dto.getNickname());
        response.put("nicknameExists", nicknameExists);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sendCode")
    public ResponseEntity<String> sendCode(@RequestBody EmailDto dto, HttpSession session) {
        try {
            String code = generateCode(); // 인증코드 생성 로직
            session.setAttribute("emailCode : " + dto.getEmail(), code); // 세션 저장

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
            helper.setTo(dto.getEmail());
            helper.setSubject("이메일 인증 코드");
            helper.setText("인증 코드 : " + code);
            mailSender.send(message);

            return ResponseEntity.ok("이메일에서 확인해주세요!");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 실패");
        }
    }
    
    private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000); // 6자리 숫자 코드
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<Boolean> verifyCode(@RequestBody EmailVerificationDto dto, HttpSession session) {
        // dto.getEmail()만 하면 다른 이메일과 충돌 가능성도 있고 가독성도 떨어짐.
        String sentCode = (String) session.getAttribute("emailCode : " + dto.getEmail());
        boolean isValid = dto.getCode().equals(sentCode);

        if(isValid) {
            session.removeAttribute("emailCode : " + dto.getEmail());
        }

        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Validated JoinMembershipDto dto) {
        try {
            userService.signup(dto);
            return ResponseEntity.ok("회원가입 성공!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}
