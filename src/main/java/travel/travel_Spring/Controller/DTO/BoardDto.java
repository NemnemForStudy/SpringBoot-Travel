package travel.travel_Spring.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 50, message = "제목은 50자 이내로 작성해주세요.")
    private String title;
    private String content;
    private String author;

    @Min(0)
    private int viewCount;

    @Min(0)
    private int likeCount;

    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime updateTime = LocalDateTime.now();

    private List<String> pictures = new ArrayList<>();

    public BoardDto(String title, String content, List<String> pictures) {
        this.title = title;
        this.content = content;
        this.pictures = pictures;
    }
}
