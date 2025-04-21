package travel.travel_Spring.Controller.DTO;

public class PasswordLengthDto {
    private String password;

    // 기본 생성자
    public PasswordLengthDto() {}

    public PasswordLengthDto(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // 비밀번호 길이가 8자 미만인지 확인하고, 유효성 체크 반환
    public boolean isValid() {
        return password != null && password.length() >= 8;
    }
}
