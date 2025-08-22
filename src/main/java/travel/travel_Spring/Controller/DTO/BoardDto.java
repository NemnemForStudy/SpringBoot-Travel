package travel.travel_Spring.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {

    @Id
    private long id;

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 50, message = "제목은 50자 이내로 작성해주세요.")
    private String title;
    private String content;
    private String author;

    @Min(0)
    private int viewCount;

    @Min(0)
    private int likeCount;
    private boolean likedByCurrentUser;

    @Column(name = "create_Time")
    private LocalDateTime createTime;

    @Column(name = "update_Time")
    private LocalDateTime updateTime;

    private String createTimeAgo;
    private List<String> pictures = new ArrayList<>();
    private long totalCount;

    public BoardDto(long id, String title, String content, List<String> pictures, LocalDateTime createTime, LocalDateTime updateTime, int likeCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.pictures = pictures;
        this.createTimeAgo = getTimeAgo(createTime);
        this.updateTime = updateTime;
        this.likeCount = likeCount;
    }

    private String getTimeAgo(LocalDateTime createTime) {
        Duration diff = Duration.between(createTime, LocalDateTime.now());

        long seconds = diff.getSeconds();
        long minutes = diff.toMinutes();
        long hours = diff.toHours();
        long days = diff.toDays();

        if(seconds < 60) {
            return seconds + "초 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if(hours < 24) {
            return hours + "시간 전";
        } else if(days < 30) {
            return days + "일 전";
        } else if(days < 365) {
            return (days / 30) + "달 전";
        } else {
            return (days / 365) + "년 전";
        }
    }
}
