document.addEventListener("DOMContentLoaded", () => {
    const boardList = document.getElementById("board-list");
    const postsPerPage = 8;
    let currentPage = 1;
    let boards = [];

    fetch("/board/travelDestination")
        .then(response => response.json())
        .then(data => {
            boards = data.boards;
            const totalPosts = data.totalCount;

            renderBoardList(boards, currentPage, postsPerPage);
            renderPagination(totalPosts, postsPerPage);
        })
        .catch(error => console.error("Error fetching boards:", error));

    function renderBoardList(boards, page, postsPerPage) {
        boardList.innerHTML = "";

        const start = (page - 1) * postsPerPage;
        const end = start + postsPerPage;
        const pageBoards = boards.slice(start, end);

        pageBoards.forEach(board => {
            const cardCol = document.createElement("div");
            cardCol.className = "col";

            cardCol.innerHTML = `
                <div class="card h-100">
                    <div class="d-flex">
                        <div class="image-box me-3">
                            <img src="${board.pictures[0] || '/images/default.jpg'}">
                        </div>
                        <div class="card-body p-0">
                            <h5 class="card-title">${board.title}</h5>
                            <p class="card-text">${board.content}</p>
                            <div class="d-flex">
                                <small class="text-muted">작성일: ${board.createTimeAgo}</small>
                                <i class="bi bi-heart-fill"></i>
                                <span class="ms-1 like-count">${board.likeCount}</span>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            cardCol.addEventListener("click", () => {
                window.location.href = `/board/boardDetailForm/${board.id}`;
            });

            boardList.appendChild(cardCol);
        });
    }
});