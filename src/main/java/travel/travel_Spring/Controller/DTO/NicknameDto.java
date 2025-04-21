package travel.travel_Spring.Controller.DTO;

public class NicknameDto {

    private String nickname;

    public NicknameDto() {}

    public NicknameDto(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() { return nickname; }

    public void setNickname(String nickname) { this.nickname = nickname; }
}
