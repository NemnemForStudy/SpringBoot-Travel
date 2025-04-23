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
import travel.travel_Spring.Service.LoginService;
import travel.travel_Spring.Service.UserService;
import travel.travel_Spring.UserEntity.User;
import travel.travel_Spring.repository.UserRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserApiController {
    @Autowired
    private UserService userService;
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private LoginService loginService;

    public UserApiController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    // 이메일 중복을 확인한다.
    // 사용가능하면 UserController -> true가 넘어와서 이 api를 사용.
    @PostMapping("/emailDuplicate")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody EmailDto dto) {
        String email = dto.getEmail();
        boolean exists = userRepository.existsByEmail(email);
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
            long timeStamp = System.currentTimeMillis(); // 현재 시간 (밀리초)

            Map<String, Object> codeData = new HashMap<>();
            codeData.put("code", code);
            codeData.put("timestamp", timeStamp);

            session.setAttribute("emailCode : " + dto.getEmail(), codeData);

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
        Enumeration<String> attributesnames = session.getAttributeNames();

        // dto.getEmail()만 하면 다른 이메일과 충돌 가능성도 있고 가독성도 떨어짐.
        String sentEmail = dto.getEmail();
        String sentCode = "";
        while (attributesnames.hasMoreElements()) {
            String key = attributesnames.nextElement();
            if(key.contains(sentEmail)) {
                Object value = session.getAttribute(key);
                Map map = (Map)value;
                sentCode = (String) map.get("code");
            }
        }

        boolean isValid = dto.getCode().equals(sentCode);

        if(isValid) {
            session.removeAttribute("emailCode : " + dto.getCode());
        }
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Validated JoinMembershipDto dto, HttpSession session) {
        Map<String, Object> codeData = (Map<String, Object>) session.getAttribute("emailCode : " + dto.getEmail());

        if(codeData == null) {
            return ResponseEntity.badRequest().body("인증 코드가 없습니다.");
        }

        String sentCode = (String) codeData.get("code");
        long sentTime = (long) codeData.get("timestamp");
        long currentTime = System.currentTimeMillis();

        // 인증 코드 만료 (3분 = 180_000ms)
        if(currentTime - sentTime > 180_000) {
            session.removeAttribute("emailCode : " + dto.getEmail());
            return ResponseEntity.badRequest().body("인증 코드가 만료되었습니다. 다시 시도해주세요.");
        }

        if(!sentCode.equals(dto.getVerificationCode())) {
            return ResponseEntity.badRequest().body("인증 코드가 일치하지 않습니다.");
        }

        try {
            userService.signup(dto);
            session.removeAttribute("emailCode : " + dto.getEmail());
            return ResponseEntity.ok("회원가입 성공!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Boolean>> login(@RequestBody LoginDto dto) {
        boolean loginSuccess = userService.login(dto.getEmail(), dto.getPassword());

        if (!loginSuccess) {
            System.out.println("로그인 실패: 이메일 또는 비밀번호 오류");
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("success", loginSuccess);

        if(loginSuccess) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
