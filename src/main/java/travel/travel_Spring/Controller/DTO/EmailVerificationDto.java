package travel.travel_Spring.Controller.DTO;

public class EmailVerificationDto {
    private String email;
    private String code;

    public EmailVerificationDto() {

    }

    public EmailVerificationDto(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
