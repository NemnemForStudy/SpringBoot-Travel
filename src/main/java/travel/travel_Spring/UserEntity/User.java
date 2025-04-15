package travel.travel_Spring.UserEntity;

import javax.persistence.*;
import java.time.LocalDate;

// model 폴더이다. 이곳에 Entity 생성.
@Entity
@Table(name ="auth_user")
public class User {
    @Id
    // 자동으로 ID 증가 시킴.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user 대신 다른 이름 사용
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // password와 같은 이름도 동일하게 변경
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    // 기본 생성자는 JPA에서 필수적 필요함.
    // 1. JPA가 객체를 생성할 때 리플렉션을 사용하기 때문이다. 강제로 new User()를 호출한 뒤, setter를 통해 값들을 주입.
    public User(String email, String password, String nickname, String phoneNumber, LocalDate birth) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
    }

    public Long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getNickname() { return nickname; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDate getBirth() {return birth; }

    public void setId(Long id) {
        this.id = id;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setBirth(LocalDate birth) { this.birth = birth; }
}
