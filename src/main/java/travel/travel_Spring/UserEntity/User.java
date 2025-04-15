package travel.travel_Spring.UserEntity;

import javax.persistence.*;

// model 폴더이다. 이곳에 Entity 생성.
@Entity
@Table(name ="auth_user")
public class User {
    @Id
    // 자동으로 ID 증가 시킴.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user 대신 다른 이름 사용
    @Column(name = "username", nullable = false, unique = true)
    private String email;

    // password와 같은 이름도 동일하게 변경
    @Column(name = "password", nullable = false)
    private String password;

    // 기본 생성자는 JPA에서 필수적 필요함.
    // 1. JPA가 객체를 생성할 때 리플렉션을 사용하기 때문이다. 강제로 new User()를 호출한 뒤, setter를 통해 값들을 주입.
    public User() {

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
