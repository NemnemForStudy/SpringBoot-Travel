package travel.travel_Spring.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "board")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Board {

    // Entity → 2. Repository → 3. DTO → 4. Service → 5. Controller
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

//    @Lob // 10000자 이상 사용한다면 Lob로 할 수 있음.
    @Column(name = "content")
    private String content;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "create_Time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_Time")
    private LocalDateTime updateTime;

    @Column(name = "viewCount")
    @Min(0)
    private int viewCount;

    @Column(name = "like_Count")
    @Min(0)
    private int likeCount;

    // Board : BoardPicture = 1 : N
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardPicture> pictures = new ArrayList<>();

    @ElementCollection
    // 이렇게 하면 board_selected_options라는 테이블을 만들어줌.
    @CollectionTable(
            name = "board_selected_options", // 별도 테이블 이름
            joinColumns = @JoinColumn(name = "board_id") // FK
    )
    @Column(name = "option_value") // 실제 옵션 값
    private List<String> selectedDropdownOptions = new ArrayList<>();

    // BoardPicture 리스트 getter
    public List<BoardPicture> getBoardPictures() {
        return pictures;
    }

    // BoardPicture 리스트 setter (선택 사항)
    public void setBoardPictures(List<BoardPicture> boardPictures) {
        this.pictures = boardPictures;
    }

    @ManyToMany
    @JoinTable(name = "board_likes", joinColumns = @JoinColumn(name = "board_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likedUsers = new HashSet<>();
}
