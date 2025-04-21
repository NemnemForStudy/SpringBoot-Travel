package travel.travel_Spring.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import travel.travel_Spring.repository.UserRepository;

import java.util.Map;

@Service
public class EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(UserRepository userRepository, JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.javaMailSender = javaMailSender;
    }

    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email) == null;  // 이메일로 사용자 조회
    }

    public void sendEmailCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("이메일 인증 코드");
        message.setText("인증 코드 : " + code);
        javaMailSender.send(message);
    }

}
