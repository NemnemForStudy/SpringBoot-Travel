package travel.travel_Spring.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.travel_Spring.Controller.Config.SecurityConfig;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Entity.BoardPicture;
import travel.travel_Spring.repository.BoardLikeRepository;
import travel.travel_Spring.repository.BoardPictureRepository;
import travel.travel_Spring.repository.BoardRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 실제 비즈니스 로직을 처리. DB와 상호작용 처리. DTO를 엔터리로 변환해 저장, 조회함.

@Slf4j
@Service
public class BoardService {

    @Autowired
    private final BoardRepository boardRepository;

    @Autowired
    private final BoardPictureRepository boardPictureRepository;

    @Autowired
    private final BoardLikeRepository likeRepository;

    public Board save(Board board) {
        return boardRepository.save(board);
    }

    public BoardService(BoardRepository boardRepository, BoardPictureRepository boardPictureRepository, BoardLikeRepository likeRepository) {
        this.boardRepository = boardRepository;
        this.boardPictureRepository = boardPictureRepository;
        this.likeRepository = likeRepository;
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

        if(dto.getPictures() != null) {
            for(String pictures : dto.getPictures()) {
                BoardPicture boardPicture = new BoardPicture();
                boardPicture.setBoard(board);
                boardPicture.setPicture(pictures);
                boardPictureRepository.save(boardPicture);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "글이 성공적으로 등록되었습니다!");
        response.put("board", board);

        return response;
    }

//    public Board getBoardById(Long id) {
//        return boardRepository.findById(id).orElseThrow(() -> new RuntimeException("글을 찾을 수 없습니다."));
//    }

    // 글 수정
//    @Transactional
//    public Board updateBoard(Long id, BoardDto dto) {
//        Board board = getBoardById(id);
//        board.setTitle(dto.getTitle());
//        board.setContent(dto.getContent());
//        board.setUpdateTime(LocalDateTime.now());
//
//        return boardRepository.save(board);
//    }

    // 글 조회
    @Transactional(readOnly = true)
    public List<BoardDto> getAllBoards() {
        // 게시글 최신순으로 보여줌.
        List<Board> boards = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));

        return boards.stream()
                .map(b -> {
                    List<String> pictures = b.getBoardPictures().stream()
                            .map(BoardPicture::getPicture)
                            .collect(Collectors.toList());
                    return new BoardDto(
                            b.getId(),
                            b.getTitle(),
                            b.getContent(),
                            pictures,
                            b.getCreateTime(),
                            b.getUpdateTime(),
                            b.getLikeCount()
                    );
                })
                .collect(Collectors.toList());
    }

    // 단일 게시글 조회 메서드
    @Transactional(readOnly = true)
    public BoardDto getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));

        List<String> pictures = board.getBoardPictures().stream()
                .map(BoardPicture::getPicture)
                .collect(Collectors.toList());

        return new BoardDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                pictures,
                board.getCreateTime(),
                board.getUpdateTime(),
                board.getLikeCount()
        );
    }

    @Transactional
    public int updateLike(Long id, boolean liked) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음 : " + id));

        if(liked) {
            board.setLikeCount(board.getLikeCount() + 1);
        } else {
            board.setLikeCount(Math.max(0, board.getLikeCount() - 1));
        }

        boardRepository.save(board);
        return board.getLikeCount();
    }

    // 글 삭제
    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Board findById(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글이 없습니다"));
    }

    // 유저가 이미 좋아요 눌렀는지 확인(User-Board 매핑 필요)
    @Transactional(readOnly = true)
    public boolean hasUserLiked(Long boardId, String email) {
        return likeRepository.existsByBoardIdAndUserEmail(boardId, email);
    }
}
