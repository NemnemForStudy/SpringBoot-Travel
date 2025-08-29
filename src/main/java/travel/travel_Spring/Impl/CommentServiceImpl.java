package travel.travel_Spring.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Entity.Comment;
import travel.travel_Spring.Entity.User;
import travel.travel_Spring.Service.CommentService;
import travel.travel_Spring.repository.BoardRepository;
import travel.travel_Spring.repository.CommentRepository;
import travel.travel_Spring.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment addComment(Long boardId, String email, String content) {
        // 1. 게시글 가져오기
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));

        // 2. 사용자 정보 가져오기(이메일)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 3. 댓글 엔티티 생성
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setAuthor(user.getNickname());
        comment.setEmail(user.getEmail());
        comment.setContent(content);

        return commentRepository.save(comment);
    }

    @Transactional
    public long getCommentCount(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        return commentRepository.countByBoardId(boardId);
    }

    @Transactional
    public List<Comment> getCommentByBoardId(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));
        return commentRepository.findAllByBoard(board);
    }

    @Transactional
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
