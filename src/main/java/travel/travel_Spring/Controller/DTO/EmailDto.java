package travel.travel_Spring.Controller.DTO;

import travel.travel_Spring.Service.EmailService;

public class EmailDto {

    private String email;

    public EmailDto() {}

    public EmailDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
