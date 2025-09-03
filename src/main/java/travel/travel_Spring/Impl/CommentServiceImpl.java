package travel.travel_Spring.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.travel_Spring.Controller.DTO.CommentResponseDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Entity.Comment;
import travel.travel_Spring.Entity.User;
import travel.travel_Spring.Service.CommentService;
import travel.travel_Spring.repository.BoardRepository;
import travel.travel_Spring.repository.CommentRepository;
import travel.travel_Spring.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public long getCommentCount(Long boardId) {
        return commentRepository.countByBoardId(boardId);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsDtoByBoardId(Long boardId, String currentUserEmail) {
        return getCommentByBoardId(boardId)
                .stream()
                .map(comment -> new CommentResponseDto(comment, currentUserEmail))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto updateComment(Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        comment.setContent(newContent);
        commentRepository.save(comment);

        String currentUserEmail = comment.getEmail();
        return new CommentResponseDto(comment, currentUserEmail);
    }

}
