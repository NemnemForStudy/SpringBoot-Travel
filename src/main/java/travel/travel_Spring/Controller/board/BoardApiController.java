package travel.travel_Spring.Controller.board;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel_Spring.Controller.Config.SecurityConfig;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Controller.DTO.BoardPictureDto;
import travel.travel_Spring.Controller.DTO.CommentRequestDto;
import travel.travel_Spring.Controller.DTO.CommentResponseDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Entity.BoardPicture;
import travel.travel_Spring.Entity.Comment;
import travel.travel_Spring.Impl.CommentServiceImpl;
import travel.travel_Spring.Service.BoardLikeService;
import travel.travel_Spring.Service.BoardService;
import travel.travel_Spring.Service.CommentService;
import travel.travel_Spring.Service.UserService;
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
    private final CommentServiceImpl commentServiceImpl;
    private final UserService userService;
    private final BoardLikeService likeService;

    // 글 작성 (POST 요청)
    @PostMapping("/write")
    public ResponseEntity<?> createBoard(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "latitude", required = false) List<String> latitudes,
            @RequestParam(value = "longitude", required = false) List<String> longitudes,
            @RequestParam(value = "selectedDropdownOptions", required = false) List<String> selectedDropdownOptions,
            @AuthenticationPrincipal LoginUserDetails userDetails) throws IOException { // 추가

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

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

        if(title == null || title.isEmpty()) { // 순서 변경
            return ResponseEntity.badRequest().body("제목을 입력해주세요");
        }

        boardRepository.save(board);

        List<String> pictureUrls = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);

                if (!file.isEmpty()) {
                    String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    System.out.println(filename);
                    Path filepath = Paths.get("uploads", filename);
                    Files.createDirectories(filepath.getParent()); // 폴더 없으면 생성
                    file.transferTo(filepath); // 서버에 저장

                    BoardPicture boardPicture = new BoardPicture();
                    boardPicture.setBoard(board);
                    boardPicture.setPictureUrl("/uploads/" + filename); // DB에는 URL만 지정

                    // 파일 순서와 동일한 Lat,Lng매핑
                    if (latitudes != null && longitudes != null && i < latitudes.size()) {
                        boardPicture.setLatitude(Double.parseDouble(latitudes.get(i)));
                        boardPicture.setLongitude(Double.parseDouble(longitudes.get(i)));
                    }

                    boardPictureRepository.save(boardPicture);
                    pictureUrls.add("/uploads/" + filename);
                }
            }
        }

        return ResponseEntity.ok(Map.of("success", true, "boardId", board.getId(), "pictures", pictureUrls));
    }

    @GetMapping("/api/travelDestination")
    public ResponseEntity<Map<String, Object>> getAllBoards(@AuthenticationPrincipal UserDetails userDetails) {
        // 비로그인 유저 처리.
        String currentUserEmail = (userDetails != null) ? userDetails.getUsername() : null;
        List<BoardDto> boards = boardService.getAllBoards(currentUserEmail); // 전체 게시글
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

    @GetMapping("/api/board/{boardId}/status")
    public ResponseEntity<Map<String, Object>> likeAndCommentAndEmailStatus(@PathVariable Long boardId, @AuthenticationPrincipal UserDetails userDetails) {
        String currentUserEmail = (userDetails != null) ? userDetails.getUsername() : "";

        Board board = boardService.findById(boardId);
        String authorNickname = board.getAuthor();
        String authorEmail = userService.getEmailByNickname(authorNickname);

        boolean liked = (!currentUserEmail.isEmpty()) && likeService.hasUserLiked(boardId, currentUserEmail);
        long likeCount = likeService.getLikeCount(boardId);
        long commentCount = commentService.getCommentCount(boardId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likeCount", likeCount);
        response.put("commentCount", commentCount);
        response.put("currentUserEmail", currentUserEmail);
        response.put("authorEmail", authorEmail != null ? authorEmail : "");

        return ResponseEntity.ok(response);
    }

    // 특정 게시글의 상세 정보, 현재 사용자가 좋아요 눌렀는지 상태 함께 제공.
    // boardDetailFunction.js Get
    @GetMapping("/api/board/{boardId}")
    public ResponseEntity<BoardDto> getBoard(@PathVariable Long boardId, Principal principal) {
        Board board = boardService.findById(boardId);

        // [수정] 비로그인 시 principal은 null이므로 안전하게 이름 추출
        String currentName = (principal != null) ? principal.getName() : null;

        // 좋아요 여부 확인 (이름이 있을 때만 서비스 호출)
        boolean likedByUser = (currentName != null) && boardService.hasUserLiked(boardId, currentName);

        // 사진/좌표 리스트 (기존 코드와 동일)
        List<BoardPictureDto> pictureDtos = board.getBoardPictures().stream()
                .sorted(Comparator.comparing(BoardPicture::getOrderIndex, Comparator.nullsLast(Integer::compareTo)))
                .map(bp -> new BoardPictureDto(bp.getFilename(), bp.getLatitude(), bp.getLongitude()))
                .collect(Collectors.toList());

        List<String> pictureUrls = board.getBoardPictures().stream()
                .sorted(Comparator.comparing(BoardPicture::getOrderIndex, Comparator.nullsLast(Integer::compareTo)))
                .map(BoardPicture::getPictureUrl)
                .collect(Collectors.toList());

        // [수정] 댓글 리스트 변환 시 안전하게 추출한 currentName 전달
        List<CommentResponseDto> commentDtos = commentService.getCommentByBoardId(boardId).stream()
                .map(comment -> new CommentResponseDto(comment, currentName))
                .collect(Collectors.toList());

        BoardDto boardDto = new BoardDto(
                board.getId(), board.getTitle(), board.getContent(), board.getEmail(),
                pictureUrls, board.getCreateTime(), board.getUpdateTime(), board.getLikeCount(),
                board.getSelectedDropdownOptions(), pictureDtos, commentDtos
        );

        boardDto.setLikedByCurrentUser(likedByUser);
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
        if(deletedImages != null) {
            for(String deleteImage : deletedImages) {
                Iterator<BoardPicture> iter = pictures.iterator();
                while (iter.hasNext()) {
                    BoardPicture picture = iter.next();
                    if(deleteImage.equals(picture.getPictureUrl())) {
                        iter.remove();
                    }
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

    // 댓글 추가
    @PostMapping("/{boardId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long boardId,
                                        @RequestBody CommentRequestDto request,
                                        @AuthenticationPrincipal LoginUserDetails userDetails) {
        if(userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인이 필요합니다."));
        }

        if(request.getContent() == null || request.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "댓글을 입력해주세요"));
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
    @GetMapping("/api/board/{boardId}/comments")
    public ResponseEntity<List<CommentResponseDto>> showCommentList(@PathVariable Long boardId, @AuthenticationPrincipal LoginUserDetails userDetails) {
        List<Comment> comments = commentService.getCommentByBoardId(boardId);
        String currentEmail = (userDetails != null) ? userDetails.getEmail() : null;

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
        // [추가] 로그인을 안 했다면 삭제 로직 자체를 실행하지 않음
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Comment comment = commentService.getCommentById(commentId);

        // [수정] userDetails가 null이 아님을 확인했으므로 이제 안전하게 getEmail() 호출 가능
        if(!comment.getEmail().equals(userDetails.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.ok("삭제 성공");
    }

    // 댓글 수정
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long commentId,
            @RequestBody Map<String, String> request
    ) {
        String newContent = request.get("content");
        CommentResponseDto updateComment = commentServiceImpl.updateComment(commentId, newContent);

        return ResponseEntity.ok(updateComment);
    }

    // 검색기능
    @GetMapping("/api/allBoards")
    @ResponseBody // DTO 리스트를 JSON 형태로 바로 반환
    public List<BoardDto> getAllBoardsForSearch() {
        return boardService.getAllBoardsForSearch();
    }
}