package travel.travel_Spring.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.travel_Spring.Entity.BoardPicture;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardPictureDto {
    private String filename;
    private Double latitude;
    private Double longitude;
//    private Integer orderIndex;

    public BoardPictureDto(BoardPicture bp) {
        this.filename = bp.getFilename();
        this.latitude = bp.getLatitude();
        this.longitude = bp.getLongitude();
//        this.orderIndex = bp.getOrderIndex();
    }
}