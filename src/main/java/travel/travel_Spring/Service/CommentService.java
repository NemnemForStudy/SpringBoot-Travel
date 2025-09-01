package travel.travel_Spring.Service;

import travel.travel_Spring.Entity.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(Long boardId, String email, String content);
    List<Comment> getCommentByBoardId(Long boardId);
    Comment getCommentById(Long commentId);
    void deleteComment(Long commentId);
}
