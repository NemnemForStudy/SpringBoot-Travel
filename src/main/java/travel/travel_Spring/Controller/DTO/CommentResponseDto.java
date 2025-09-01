package travel.travel_Spring.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import travel.travel_Spring.Entity.Comment;

import javax.persistence.Column;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private long id;
    private String author;
    private String email;
    private String content;
    private LocalDateTime createTime;
    private String createTimeAgo;
    private String currentUserEmail;

    public CommentResponseDto(Comment commentEntity, String currentUserEmail) {
        this.id = commentEntity.getCommentId();
        this.author = commentEntity.getAuthor();
        this.email = commentEntity.getEmail();
        this.content = commentEntity.getContent();
        this.createTimeAgo = getTimeAgo(commentEntity.getCreateTime());
        this.currentUserEmail = currentUserEmail;
    }

    private String getTimeAgo(LocalDateTime createTime) {
        Duration diff = Duration.between(createTime, LocalDateTime.now());
        long seconds = diff.getSeconds();
        long minutes = diff.toMinutes();
        long hours = diff.toHours();
        long days = diff.toDays();

        if(seconds < 60) return seconds + "초 전";
        else if(minutes < 60) return minutes + "분 전";
        else if(hours < 24) return hours + "시간 전";
        else if(days < 30) return days + "일 전";
        else if(days < 365) return (days / 30) + "달 전";
        else return (days / 365) + "년 전";
    }
}
