package travel.travel_Spring.Controller.BCryptEncryptor;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BCryptEncryptor implements Encryptor {

    // 원본 비밀번호 BCrypt 해시로 암호화 메서드
    @Override
    public String encrypt(String origin) {
        return BCrypt.hashpw(origin, BCrypt.gensalt());
    }

    @Override
    public boolean isMatch(String origin, String hashed) {
        try {
            return BCrypt.checkpw(origin, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}
