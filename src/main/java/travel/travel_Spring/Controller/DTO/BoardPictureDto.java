package travel.travel_Spring.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.travel_Spring.Entity.BoardPicture;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardPictureDto {
    private Double latitude;
    private Double longitude;

    public BoardPictureDto(BoardPicture bp) {
        this.latitude = bp.getLatitude();
        this.longitude = bp.getLongitude();
    }
}