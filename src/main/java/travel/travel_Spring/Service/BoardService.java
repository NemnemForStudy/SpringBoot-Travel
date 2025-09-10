package travel.travel_Spring.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.travel_Spring.Controller.Config.SecurityConfig;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Controller.DTO.BoardPictureDto;
import travel.travel_Spring.Controller.DTO.CommentResponseDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Entity.BoardPicture;
import travel.travel_Spring.Entity.User;
import travel.travel_Spring.Impl.CommentServiceImpl;
import travel.travel_Spring.repository.*;

import java.time.LocalDateTime;
import java.util.*;
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

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CommentServiceImpl commentService;

    @Autowired
    private final CommentRepository commentRepository;

    public Board save(Board board) {
        return boardRepository.save(board);
    }

    public BoardService(BoardRepository boardRepository, BoardPictureRepository boardPictureRepository, BoardLikeRepository likeRepository, UserRepository userRepository, CommentServiceImpl commentService, CommentRepository commentRepository) {
        this.boardRepository = boardRepository;
        this.boardPictureRepository = boardPictureRepository;
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.commentService = commentService;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Map<String, Object> createBoard(BoardDto dto) {
        Board board = new Board();
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setAuthor(SecurityConfig.getCurrentNickname());
        board.setEmail(dto.getEmail());
        board.setCreateTime(LocalDateTime.now());
        board.setUpdateTime(LocalDateTime.now());
        board.setViewCount(0);
        board.setLikeCount(0);

        boardRepository.save(board);

        // pictures 처리
        if(dto.getPictures() != null) {
            List<BoardPicture> pictureEntities = new ArrayList<>();

            for(String pictureUrl : dto.getPictures()) {
                BoardPicture boardPicture = new BoardPicture();
                boardPicture.setBoard(board);
                boardPicture.setPictureUrl(pictureUrl);
                pictureEntities.add(boardPicture);
            }
            // 한번에 저장
            // saveAll 하면 자동으로 FK(board_id)에 연결됨.
            boardPictureRepository.saveAll(pictureEntities);

            // Board의 boardPictures 리스트에도 추가(양방향 연관관계)
            board.setBoardPictures(pictureEntities);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "글이 성공적으로 등록되었습니다!");
        response.put("board", board);

        return response;
    }

    // 글 조회
    @Transactional(readOnly = true)
    public List<BoardDto> getAllBoards(String currentUserEmail) {
        // 게시글 최신순으로 보여줌.
        List<Board> boards = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));

        return boards.stream()
                .map(b -> {
                    List<String> pictures = b.getBoardPictures().stream()
                            .map(BoardPicture::getPictureUrl)
                            .collect(Collectors.toList());

                    List<BoardPictureDto> pictureDtos = b.getBoardPictures().stream()
                            .map(BoardPictureDto::new)
                            .collect(Collectors.toList());

                    List<CommentResponseDto> comments = commentService.getCommentByBoardId(b.getId()).stream()
                            .map(c -> new CommentResponseDto(c, currentUserEmail))  // Comment 객체 전체와 현재 로그인 유저 이메일 전달
                            .collect(Collectors.toList());

                    return new BoardDto(
                            b.getId(),
                            b.getTitle(),
                            b.getContent(),
                            b.getEmail(),
                            pictures,
                            b.getCreateTime(),
                            b.getUpdateTime(),
                            b.getLikeCount(),
                            b.getSelectedDropdownOptions(),
                            pictureDtos,
                            comments
                    );
                })
                .collect(Collectors.toList());
    }

    // 단일 게시글 조회 메서드
    @Transactional(readOnly = true)
    public BoardDto getBoardById(Long id, String currentUserEmail) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));

        List<String> pictures = board.getBoardPictures().stream()
                .map(BoardPicture::getPictureUrl)
                .collect(Collectors.toList());

        List<BoardPictureDto> pictureDtos = board.getBoardPictures().stream()
                .map(BoardPictureDto::new)
                .collect(Collectors.toList());

        List<CommentResponseDto> comments = commentService.getCommentByBoardId(id).stream()
                .map(comment -> new CommentResponseDto(comment, currentUserEmail))
                .collect(Collectors.toList());

        return new BoardDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getEmail(),
                pictures,
                board.getCreateTime(),
                board.getUpdateTime(),
                board.getLikeCount(),
                board.getSelectedDropdownOptions(),
                pictureDtos,
                comments
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

    @Transactional
    public Board updateBoard(Long id, String title, String content) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        board.setTitle(title);
        board.setContent(content);

        return boardRepository.save(board);
    }

    // 글 삭제
    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    // 이메일 조회
    @Transactional
    public String getAuthorEmail(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID : " + boardId));
        return board.getEmail();
    }

    @Transactional(readOnly = true)
    public Board findById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));
    }

    @Transactional
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 유저가 이미 좋아요 눌렀는지 확인(User-Board 매핑 필요)
    @Transactional(readOnly = true)
    public boolean hasUserLiked(Long boardId, String email) {
        return likeRepository.existsByBoardIdAndUserEmail(boardId, email);
    }

    public List<Board> searchByTitle(String query) {
        return boardRepository.findByTitleContaining(query);
    }

    public List<BoardDto> searchByTitleAsDto(String query) {
        List<Board> boards = boardRepository.findByTitleContaining(query);
        return boards.stream().map(board -> {
            List<String> pictureUrls = board.getPictures().stream()
                    .map(BoardPicture::getPictureUrl)
                    .collect(Collectors.toList());

            long commentCount = commentRepository.countByBoardId(board.getId());

            BoardDto dto = new BoardDto();
            dto.setId(board.getId());
            dto.setTitle(board.getTitle());
            dto.setContent(board.getContent());
            dto.setPictures(pictureUrls);
            dto.setLikeCount(board.getLikeCount());
            dto.setCommentCount(commentCount);
            dto.setCreateTimeAgo(board.getCreateTimeAgo());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BoardDto> searchBoards(String query, String currentUserEmail) {
        // 1. 검색어를 사용해 제목 or 내용으로 게시글 찾기
        List<Board> boards = boardRepository.findByTitleContainingOrContentContaining(query, query);

        // 2. 게시글 리스트를 BoardDto로 변환
        return boards.stream()
                .map(b -> {
                    // BoardDto 생성에 필요한 데이터 가공
                    List<String> pictures = b.getBoardPictures().stream()
                            .map(BoardPicture::getPictureUrl)
                            .collect(Collectors.toList());

                    List<CommentResponseDto> comments = commentService.getCommentByBoardId(b.getId()).stream()
                            .map(c -> new CommentResponseDto(c, currentUserEmail))
                            .collect(Collectors.toList());

                    // 3. BoardDto 객체 생성 및 반환
                    return new BoardDto(
                            b.getId(),
                            b.getTitle(),
                            b.getContent(),
                            b.getEmail(),
                            pictures,
                            b.getCreateTime(),
                            b.getUpdateTime(),
                            b.getLikeCount(),
                            b.getSelectedDropdownOptions(),
                            // 검색 결과에도 댓글 포함하려면 이 필드 사용
                            null, // pictureDtos
                            comments
                    );
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BoardDto> getAllBoardsForSearch() {
        // 최신순으로 게시글 가져옴
        List<Board> boards = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));

        // 게시글 제목, 내용, 사진 등 검색에 필요한 최소한 데이터만 담아 반환
        return boards.stream()
                .map(b -> {
                    List<String> pictures = b.getBoardPictures().stream()
                            .map(BoardPicture::getPictureUrl)
                            .collect(Collectors.toList());

                    return new BoardDto(
                            b.getId(),
                            b.getTitle(),
                            b.getContent(),
                            b.getEmail(),
                            pictures,
                            b.getCreateTime(),
                            b.getUpdateTime(),
                            b.getLikeCount(),
                            b.getSelectedDropdownOptions()
                    );
                }).collect(Collectors.toList());
    }
}
