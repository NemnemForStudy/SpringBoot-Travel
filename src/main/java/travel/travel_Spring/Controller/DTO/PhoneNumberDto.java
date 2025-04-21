package travel.travel_Spring.Controller.DTO;

public class PhoneNumberDto {
    private String phoneNumber;

    // 기본 생성자
    public PhoneNumberDto() {}

    public PhoneNumberDto(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isValidPhoneNumber() {
        return phoneNumber != null && phoneNumber.matches("^010-\\d{4}-\\d{4}$");
    }
}
