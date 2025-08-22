package travel.travel_Spring.Controller.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@Getter
@Setter
public class JoinMembershipDto {

    private String email;
    private String username;
    private String password;
    private BCryptPasswordEncoder passwordEncoder;
    private String nickname;
    private String phoneNumber;
    private String birthYear;
    private String birthMonth;
    private String birthDay;
    private String code;

    public LocalDate getBirth() {
        try {
            if (birthYear == null || birthMonth == null || birthDay == null ||
                    birthYear.isEmpty() || birthMonth.isEmpty() || birthDay.isEmpty()) {
                throw new IllegalArgumentException("생년월일을 올바르게 입력하세요.");
            }

            int year = Integer.parseInt(birthYear);  // String -> int로 변환
            int month = Integer.parseInt(birthMonth);  // String -> int로 변환
            int day = Integer.parseInt(birthDay);  // String -> int로 변환
            return LocalDate.of(year, month, day);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("생년월일 형식이 올바르지 않습니다.");
        }
    }
}
