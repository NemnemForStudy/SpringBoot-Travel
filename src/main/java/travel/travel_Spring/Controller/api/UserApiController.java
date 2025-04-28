package travel.travel_Spring.Controller.api;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel_Spring.Controller.DTO.*;
import travel.travel_Spring.Service.EmailService;
import travel.travel_Spring.Service.LoginService;
import travel.travel_Spring.Service.UserService;
import travel.travel_Spring.UserDetails.LoginUserDetails;
import travel.travel_Spring.repository.UserRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        String email = dto.getEmail();
        if(emailService.isEmailExists(email)) {
            return ResponseEntity.badRequest().body("이미 회원가입 된 이메일입니다.");
        }

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
    public ResponseEntity<Map<String, Object>> signup(@RequestBody @Validated JoinMembershipDto dto, HttpSession session) {
            Map<String, Object> codeData = (Map<String, Object>) session.getAttribute("emailCode : " + dto.getEmail());

        Map<String ,Object> response = new HashMap<>();
        if(codeData == null) {
            response.put("success", false);
            response.put("message", "인증코드가 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        String sentCode = (String) codeData.get("code");
        long sentTime = (long) codeData.get("timestamp");
        long currentTime = System.currentTimeMillis();

        // 인증 코드 만료 (3분 = 180_000ms)
        if(currentTime - sentTime > 180_000) {
            session.removeAttribute("emailCode : " + dto.getEmail());
            response.put("success", false);
            response.put("message", "인증 코드가 만료되었습니다. 다시 시도해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        if(!sentCode.equals(dto.getCode())) {
            response.put("success", false);
            response.put("message", "인증 코드가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            userService.signup(dto);
            session.removeAttribute("emailCode : " + dto.getEmail());

            response.put("success", true);
            response.put("message", "회원가입 성공!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 프로필 이미지 조회용 API
    @GetMapping("/profileImg")
    public ResponseEntity<String> getProfileImg(@AuthenticationPrincipal LoginUserDetails userDetails) {
        String imgUrl = userService.getProfileImg(userDetails.getUsername());
        return ResponseEntity.ok(imgUrl);
    }

    @PostMapping("/profileUpload")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("file")MultipartFile file,
                                                @AuthenticationPrincipal LoginUserDetails userDetails) {
        if(file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 없습니다.");
        }

        try {
            // 파일 저장 경로 설정
            String projectDir = System.getProperty("user.dir");
            String uploadDir = projectDir + "/src/main/resources/static/Image/";
            File directory = new File(uploadDir);
            if(!directory.exists()) directory.mkdirs(); // 디렉토리 없으면 생성.

            // 파일 이름 설정(UUID로 중복 방지)
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);

            file.transferTo(dest);

            String imgUrl = "/Image/" + fileName;
            userService.updateProfileImg(userDetails.getUsername(), imgUrl);

            return ResponseEntity.ok(imgUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("파일 업로드 실패 : " + e.getMessage());
        }
    }

    @PostMapping("/updateNickname")
    public ResponseEntity<Map<String, Object>> updateNickname(@RequestBody NicknameDto dto, @AuthenticationPrincipal LoginUserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            userService.updateNickname(userDetails.getUsername(), dto.getNickname());
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
