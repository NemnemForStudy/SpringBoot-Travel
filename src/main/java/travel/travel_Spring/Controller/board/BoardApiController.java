package travel.travel_Spring.Controller.board;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.travel_Spring.Controller.Config.SecurityConfig;
import travel.travel_Spring.Controller.DTO.BoardCountDto;
import travel.travel_Spring.Controller.DTO.BoardDto;
import travel.travel_Spring.Entity.Board;
import travel.travel_Spring.Entity.BoardPicture;
import travel.travel_Spring.Service.BoardLikeService;
import travel.travel_Spring.Service.BoardService;
import travel.travel_Spring.Service.CardService;
import travel.travel_Spring.repository.BoardPictureRepository;
import travel.travel_Spring.repository.BoardRepository;

import org.springframework.data.domain.*;

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
    private final CardService cardService;
    private final BoardRepository boardRepository;
    private final BoardPictureRepository boardPictureRepository;
    private final BoardLikeService likeService;

    // 글 작성 (POST 요청)
    @PostMapping("/write")
    public ResponseEntity<?> createBoard(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "selectedDropdownOptions", required = false) List<String> selectedDropdownOptions) throws IOException {

        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);
        board.setAuthor(SecurityConfig.getCurrentNickname());
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

    // 게시글 리스트 조회
    @GetMapping("/travelDestination")
    public ResponseEntity<Map<String, Object>> getTravelBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Board> boardPage = boardRepository.findAll(pageable);

        List<BoardDto> boards = boardPage.stream()
                .map(b -> new BoardDto(
                        b.getId(),
                        b.getTitle(),
                        b.getContent(),
                        b.getPictures().stream().map(BoardPicture::getPictureUrl).collect(Collectors.toList()),
                        b.getCreateTime(),          // 필수! createTimeAgo 계산 위해
                        b.getUpdateTime(),
                        b.getLikeCount(),
                        b.getSelectedDropdownOptions()
                ))
                .collect(Collectors.toList());


        Map<String, Object> response = new HashMap<>();
        response.put("boards", boards);
        response.put("currentPage", boardPage.getNumber());
        response.put("totalPages", boardPage.getTotalPages());
        response.put("totalElements", boardPage.getTotalElements());

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
        boardRepository.save(board);

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
        return ResponseEntity.ok(Map.of("success", true));
    }
}