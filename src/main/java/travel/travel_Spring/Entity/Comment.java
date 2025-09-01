package travel.travel_Spring.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CommentTable")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment {

    // 댓글이 게시글(Board)에 달려있으니 @ManyToOne으로
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "create_Time", nullable = false)
    private LocalDateTime createTime;

    // 이렇게 사용하면 댓글 등록 시 createTime이 자동으로 생성된다 함.
    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }
}
