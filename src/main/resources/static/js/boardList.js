function renderSearchResults(boards) {
    const boardList = document.getElementById("board-list");
    boardList.innerHTML = "";

    boards.forEach(board => {
        const col = document.createElement("div");
        col.className = "col-12 col-md-6 col-lg-4 mb-3";

        const commentCount = board.commentList ? board.commentList.length : 0;

        col.innerHTML = `
            <div class="card h-100">
                <div class="d-flex">
                    <div class="image-box me-3">
                        <img src="${board.pictures && board.pictures.length > 0 ? board.pictures[0] : '/images/default.jpg'}">
                    </div>
                    <div class="card-body p-0">
                        <h5 class="card-title">${board.title}</h5>
                        <p class="card-text">${board.content}</p>
                        <div class="d-flex">
                            <small class="text-muted">작성일: ${board.createTimeAgo}</small>
                            <i style="margin-left: 5px;" class="bi bi-heart-fill"></i>
                            <span class="ms-1 like-count">${board.likeCount}</span>
                            <i style="margin-left: 5px;" class="bi bi-chat-square"></i>
                            <span class="ms-1 comment-count">${commentCount}</span>
                        </div>
                    </div>
                </div>
            </div>
        `;

        col.addEventListener("click", () => {
            window.location.href = `/board/boardDetailForm/${board.id}`;
        });

        boardList.appendChild(col);
    });
}
