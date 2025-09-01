package travel.travel_Spring.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Entity.BoardLike;
import travel.travel_Spring.Entity.User;
import travel.travel_Spring.repository.BoardLikeRepository;
import travel.travel_Spring.repository.BoardRepository;
import travel.travel_Spring.repository.UserRepository;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BoardLikeService {
    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleLike(Long boardId, String username) {
        // 게시글 & 유저 조회
        Board board = boardRepository.findById(boardId).orElseThrow();
        User user = userRepository.findByEmail(username).orElseThrow();

        boolean liked;
        Set<User> likedUsers = board.getLikedUsers();

        if (likedUsers.contains(user)) {
            // 이미 좋아요 눌렀으면 취소
            likedUsers.remove(user);
            board.setLikeCount(Math.max(0, board.getLikeCount() - 1));
            liked = false;
        } else {
            // 좋아요 추가
            likedUsers.add(user);
            board.setLikeCount(board.getLikeCount() + 1);
            liked = true;
        }

        boardRepository.save(board); // 변경사항 저장
        return liked; // 현재 좋아요 상태 반환
    }

    @Transactional(readOnly = true)
    public boolean hasUserLiked(Long boardId, String email) {
        return boardLikeRepository.existsByBoardIdAndUserEmail(boardId, email);
    }

    public long getLikeCount(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        return boardLikeRepository.countByBoardId(boardId);
    }
}
