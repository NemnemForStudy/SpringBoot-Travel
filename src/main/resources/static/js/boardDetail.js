document.addEventListener("DOMContentLoaded", () => {
    // URL에서 게시글 ID 가져오기
    const urlParts = window.location.pathname.split("/");
    const boardId = urlParts[urlParts.length - 1];

    fetch(`/board/api/board/${boardId}`)
        .then(res => res.json())
        .then(board => {
            debugger;
            document.getElementById("title").textContent = board.title;
            document.getElementById("content").textContent = board.content;
            document.getElementById("createTimeAgo").textContent = board.createTimeAgo;
            document.getElementById("likeCount").textContent = board.likeCount;  
        })
        .catch(err => console.error(err));
});