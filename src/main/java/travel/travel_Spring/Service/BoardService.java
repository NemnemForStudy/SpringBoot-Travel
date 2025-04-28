package travel.travel_Spring.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.travel_Spring.Controller.Config.SecurityConfig;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.UserDetails.LoginUserDetails;
import travel.travel_Spring.repository.BoardRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// 실제 비즈니스 로직을 처리. DB와 상호작용 처리. DTO를 엔터리로 변환해 저장, 조회함.

@Slf4j
@Service
public class BoardService {

    @Autowired
    private final BoardRepository boardRepository;

    public Board save(Board board) {
        return boardRepository.save(board);
    }

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public Map<String, Object> createBoard(BoardDto dto) {
        Board board = new Board();
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());

        board.setAuthor(SecurityConfig.getCurrentNickname());
        board.setCreateTime(LocalDateTime.now());
        board.setUpdateTime(LocalDateTime.now());
        board.setViewCount(0);
        board.setLikeCount(0);

        boardRepository.save(board);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "글이 성공적으로 등록되었습니다!");
        response.put("board", board);

        return response;
    }

    public Board getBoardById(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new RuntimeException("글을 찾을 수 없습니다."));
    }

    // 글 수정
    @Transactional
    public Board updateBoard(Long id, BoardDto dto) {
        Board board = getBoardById(id);
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setUpdateTime(LocalDateTime.now());

        return boardRepository.save(board);
    }

    // 글 삭제
    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }
}
