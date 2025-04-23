package travel.travel_Spring.Controller.DTO;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

public class JoinMembershipDto {

    private String email;
    private String password;
    private BCryptPasswordEncoder passwordEncoder;
    private String nickname;
    private String phoneNumber;
    private String birthYear;
    private String birthMonth;
    private String birthDay;
    private String verificationCode;

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    // Getter, Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(String birthMonth) {
        this.birthMonth = birthMonth;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public BCryptPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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
