package travel.travel_Spring.Controller.board;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Impl.CommentServiceImpl;
import travel.travel_Spring.Service.BoardLikeService;
import travel.travel_Spring.Service.BoardService;
import travel.travel_Spring.Service.UserService;
import travel.travel_Spring.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class BoardLikeController {
    private final BoardLikeService likeService;
    private final BoardService boardService;
    private final UserService userService;
    private final CommentServiceImpl commentService;

    // 서버에서 자동으로 좋아요 상태 토글.
    @PostMapping("/{boardId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long boardId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        boolean liked = likeService.toggleLike(boardId, username);
        long count = likeService.getLikeCount(boardId);

        return ResponseEntity.ok(Map.of("liked", liked, "likeCount", count));
    }

    @GetMapping("/{boardId}/status")
    public ResponseEntity<Map<String, Object>> likeAndCommentAndEmailStatus(@PathVariable Long boardId, @AuthenticationPrincipal UserDetails userDetails) {
        String currentUserEmail = userDetails.getUsername();

        Board board = boardService.findById(boardId);
        String authorNickname = board.getAuthor();
        String authorEmail = userService.getEmailByNickname(authorNickname);

        boolean liked = likeService.hasUserLiked(boardId, currentUserEmail);
        long likeCount = likeService.getLikeCount(boardId);
        long commentCount = commentService.getCommentCount(boardId);

        return ResponseEntity.ok(Map.of(
                "liked", liked,
                "likeCount", likeCount,
                "commentCount", commentCount,
                "currentUserEmail", currentUserEmail,
                "authorEmail", authorEmail
        ));
    }
}
