package travel.travel_Spring.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Entity.BoardPicture;
import travel.travel_Spring.Entity.Comment;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    
    private long id;

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 50, message = "제목은 50자 이내로 작성해주세요.")
    private String title;
    private String content;
    private String author;
    private String email;

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
    
    // 이미지 url 리스트
    private List<String> pictures = new ArrayList<>();
    private long totalCount;
    
    // 드롭다운 값
    private List<String> selectedDropdownOptions = new ArrayList<>();

    private List<BoardPictureDto> pictureDtos = new ArrayList<>();

    private long commentCount;
    private List<CommentResponseDto> commentList = new ArrayList<>();

    public BoardDto(long id,
                    String title,
                    String content,
                    String email,
                    List<String> pictures,
                    LocalDateTime createTime,
                    LocalDateTime updateTime,
                    int likeCount,
                    List<String> selectedDropdownOptions,
                    List<BoardPictureDto> pictureDtos,
                    List<CommentResponseDto> commentList) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.email = email;
        this.pictures = pictures;
        this.createTimeAgo = getTimeAgo(createTime);
        this.updateTime = updateTime;
        this.likeCount = likeCount;
        this.selectedDropdownOptions = selectedDropdownOptions;
        this.pictureDtos = pictureDtos;
        this.commentList = commentList;
    }

    public BoardDto(Long id, String title, List<BoardPictureDto> pictureDtos, List<CommentResponseDto> commentList) {
        this.id = id;
        this.title = title;
        this.pictureDtos = pictureDtos;
        this.commentList = commentList;
    }

    public BoardDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createTimeAgo = getTimeAgo(board.getCreateTime());

        if(board.getPictures() != null) {
            this.pictureDtos = board.getPictures().stream()
                    .map(p -> new BoardPictureDto(p.getFilename(), p.getLatitude(), p.getLongitude()))
                    .collect(Collectors.toList());
        }
    }

    // 검색 전용으로 새롭게 추가할 생성자 (댓글 제외)
    public BoardDto(long id, String title, String content, String email, List<String> pictures,
                    LocalDateTime createTime, LocalDateTime updateTime, int likeCount,
                    List<String> selectedDropdownOptions) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.email = email;
        this.pictures = pictures;
        this.createTimeAgo = getTimeAgo(createTime);
        this.updateTime = updateTime;
        this.likeCount = likeCount;
        this.selectedDropdownOptions = selectedDropdownOptions;
        // Comment 관련 필드는 초기화하지 않음
        this.commentList = null;
        this.pictureDtos = null; // 필요 없다면 이것도 null 처리 가능
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
