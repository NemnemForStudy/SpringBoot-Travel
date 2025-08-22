package travel.travel_Spring.Controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import travel.travel_Spring.Service.BoardLikeService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class BoardLikeController {
    private final BoardLikeService likeService;

    // 서버에서 자동으로 좋아요 상태 토글.
    @PostMapping("/{boardId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long boardId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        boolean liked = likeService.toggleLike(boardId, username);
        long count = likeService.getLikeCount(boardId);

        return ResponseEntity.ok(Map.of("liked", liked, "likeCount", count));
    }

    @GetMapping("/{boardId}/status")
    public ResponseEntity<Map<String, Object>> likeStatus(@PathVariable Long boardId, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        boolean liked = likeService.hasUserLiked(boardId, email);
        long count = likeService.getLikeCount(boardId);

        return ResponseEntity.ok(Map.of("liked", liked, "likeCount", count));
    }
}
