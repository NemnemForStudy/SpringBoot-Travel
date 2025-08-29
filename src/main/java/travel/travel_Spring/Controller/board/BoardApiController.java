package travel.travel_Spring.Controller.board;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel_Spring.Controller.Config.SecurityConfig;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Controller.DTO.CommentRequestDto;
import travel.travel_Spring.Controller.DTO.CommentResponseDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Entity.BoardPicture;
import travel.travel_Spring.Entity.Comment;
import travel.travel_Spring.Service.BoardService;
import travel.travel_Spring.Service.CommentService;
import travel.travel_Spring.UserDetails.LoginUserDetails;
import travel.travel_Spring.repository.BoardPictureRepository;
import travel.travel_Spring.repository.BoardRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController // JSON 변환용(API)
@RequestMapping("/board")
@RequiredArgsConstructor  // 생성자 주입을 위한 어노테이션
public class BoardApiController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final BoardPictureRepository boardPictureRepository;
    private final CommentService commentService;

    // 글 작성 (POST 요청)
    @PostMapping("/write")
    public ResponseEntity<?> createBoard(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "selectedDropdownOptions", required = false) List<String> selectedDropdownOptions) throws IOException {

        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);
        board.setAuthor(SecurityConfig.getCurrentNickname());
        board.setEmail(SecurityConfig.getCurrentEmail());
        board.setCreateTime(LocalDateTime.now());
        board.setUpdateTime(LocalDateTime.now());
        board.setViewCount(0);
        board.setLikeCount(0);
        board.setSelectedDropdownOptions(selectedDropdownOptions);
        boardRepository.save(board);

        List<String> pictureUrls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path filepath = Paths.get("uploads", filename);
                    Files.createDirectories(filepath.getParent()); // 폴더 없으면 생성
                    file.transferTo(filepath); // 서버에 저장

                    BoardPicture boardPicture = new BoardPicture();
                    boardPicture.setBoard(board);
                    boardPicture.setPictureUrl("/uploads/" + filename); // DB에는 URL만 지정
                    boardPictureRepository.save(boardPicture);

                    pictureUrls.add("/uploads/" + filename);
                }
            }
        }

        return ResponseEntity.ok(Map.of("success", true, "boardId", board.getId(), "pictures", pictureUrls));
    }

    @GetMapping("/travelDestination")
    public ResponseEntity<Map<String, Object>> getAllBoards() {
        List<BoardDto> boards = boardService.getAllBoards(); // 전체 게시글
        Map<String, Object> response = new HashMap<>();
        response.put("boards", boards);
        response.put("totalCount", boards.size());
        return ResponseEntity.ok(response);
    }

    // 게시글의 좋아요 추가/삭제하고, 최신 좋아요 수 클라이언트에 전달.
    // boardDetailFunction.js Post
    @PostMapping("/api/board/{id}/like")
    public ResponseEntity<Map<String, Object>> likeBoard(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        boolean liked = request.get("liked");
        int updatedCount = boardService.updateLike(id, liked);
        return ResponseEntity.ok(Map.of("success", true, "likeCount", updatedCount));
    }

    // 특정 게시글의 상세 정보, 현재 사용자가 좋아요 눌렀는지 상태 함께 제공.
    // boardDetailFunction.js Get
    @GetMapping("/api/board/{boardId}")
    public ResponseEntity<BoardDto> getBoard(@PathVariable Long boardId, Principal principal) {
        Board board = boardService.findById(boardId);
        boolean likedByUser = boardService.hasUserLiked(boardId, principal.getName());

        BoardDto boardDto = new BoardDto();
        boardDto.setId(board.getId());
        boardDto.setTitle(board.getTitle());
        boardDto.setContent(board.getContent());
        boardDto.setLikeCount(board.getLikeCount());
        boardDto.setLikedByCurrentUser(likedByUser);
        boardDto.setSelectedDropdownOptions(board.getSelectedDropdownOptions());

        // 사진 URL 리스트 세팅
        List<String> pictureUrls = board.getPictures().stream()
                .map(BoardPicture::getPictureUrl)
                .collect(Collectors.toList());
        boardDto.setPictures(pictureUrls);

        return ResponseEntity.ok(boardDto);
    }

    // 게시물 수정
    @PutMapping("/api/update/{boardId}")
    public ResponseEntity<?> updateBoard(@PathVariable Long boardId,
                                         @RequestParam String title,
                                         @RequestParam String content,
                                         @RequestParam(value="files", required = false) List<MultipartFile> files,
                                         @RequestParam(value="deletedImages", required = false) List<String> deletedImages,
                                         @RequestParam(value="selectedDropdownOptions", required = false) List<String> selectedDropdownOptions
    ) throws IOException {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new RuntimeException("게시글이 없습니다."));
        board.setTitle(title);
        board.setContent(content);

        // 드롭다운 값 업데이트
        if(selectedDropdownOptions != null) {
            board.setSelectedDropdownOptions(selectedDropdownOptions);
        }
        board.setUpdateTime(LocalDateTime.now());

        // 새로 업로드 된 파일 처리
        if(files != null && !files.isEmpty()) {
            for(MultipartFile file : files) {
                if(!file.isEmpty()) {
                    String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    Path filepath = Paths.get("uploads", filename);
                    Files.createDirectories(filepath.getParent());
                    file.transferTo(filepath);

                    BoardPicture boardPicture = new BoardPicture();
                    boardPicture.setBoard(board);
                    boardPicture.setPictureUrl("/uploads/" + filename);
                    boardPictureRepository.save(boardPicture);
                }
            }
        }

        // List<BoardPicture> 에서 제거할 때는 BoardPicture 객체 자체 제거해야함.
        List<BoardPicture> pictures = board.getPictures();
        for(String deleteImage : deletedImages) {
            Iterator<BoardPicture> iter = pictures.iterator();
            while (iter.hasNext()) {
                BoardPicture picture = iter.next();
                if(deleteImage.equals(picture.getPictureUrl())) {
                    iter.remove();
                }
            }
        }
        boardRepository.save(board);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Map<String, String>> deleteBoard(@PathVariable Long boardId,
                                                           @AuthenticationPrincipal LoginUserDetails userDetails) {
        Map<String, String> response = new HashMap<>();
        Board board = boardService.findById(boardId);

        if(board == null) {
            response.put("message", "게시물이 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // 작성자만 삭제 가능
        if(!board.getEmail().equals(userDetails.getEmail())) {
            response.put("message", "삭제 권한이 없습니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        boardService.deleteBoard(boardId);
        response.put("message", "삭제 성공");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{boardId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long boardId,
                                        @RequestBody CommentRequestDto request,
                                        @AuthenticationPrincipal LoginUserDetails userDetails) {
        if(request.getContent().isEmpty() || request.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "댓글을 입력해주세요"));
        }

        Comment comment = commentService.addComment(boardId, userDetails.getEmail(), request.getContent());

        return ResponseEntity.ok(Map.of(
                "id", comment.getCommentId(),
                "author", comment.getAuthor(),
                "email", comment.getEmail(),
                "content", comment.getContent(),
                "createTime", comment.getCreateTime()
        ));
    }

    // 댓글 리스트
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<List<CommentResponseDto>> showCommentList(@PathVariable Long boardId, @AuthenticationPrincipal LoginUserDetails userDetails) {
        List<Comment> comments = commentService.getCommentByBoardId(boardId);
        String currentEmail = userDetails.getEmail();

        // Comment -> CommentResponseDto 변환
        List<CommentResponseDto> response = comments.stream()
                .map(c -> new CommentResponseDto(c, currentEmail))
                .toList();

        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                         @AuthenticationPrincipal LoginUserDetails userDetails) {
        // 댓글 가져오기
        Comment comment = commentService.getCommentById(commentId);

        // 현재 사용자와 작성자 비교
        if(!comment.getEmail().equals(userDetails.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }

        // 댓글 삭제
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("삭제 성공");
    }
}