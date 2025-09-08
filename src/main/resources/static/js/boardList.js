document.addEventListener("DOMContentLoaded", async () => {
    const boardList = document.getElementById("board-list");
    const paginationContainer = document.querySelector("ul.pagination"); // 정확히 ul 선택
    const postsPerPage = 8;

    let boards = [];
    let currentPage = 1;

    // 전체 게시글 fetch
    try {
        const response = await fetch("/board/travelDestination");
        const data = await response.json();
        boards = data.boards || [];

        renderBoardList();
        renderPagination();
    } catch (error) {
        console.error("Error fetching boards:", error);
    }

    function renderBoardList() {
        boardList.innerHTML = "";

        const start = (currentPage - 1) * postsPerPage;
        const end = start + postsPerPage;
        const pageBoards = boards.slice(start, end);

        pageBoards.forEach(board => {
            const cardCol = document.createElement("div");
            const commentCount = board.commentList.length;
            
            cardCol.className = "col";
            cardCol.innerHTML = `
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

            cardCol.addEventListener("click", () => {
                window.location.href = `/board/boardDetailForm/${board.id}`;
            });

            boardList.appendChild(cardCol);
        });
    }

    function renderPagination() {
        paginationContainer.innerHTML = "";
        const totalPosts = boards.length;
        const totalPages = Math.ceil(totalPosts / postsPerPage);

        // Previous
        const prevLi = document.createElement("li");
        prevLi.className = "page-item" + (currentPage === 1 ? " disabled" : "");
        prevLi.innerHTML = `<a class="page-link" href="#" data-page="${currentPage - 1}">Previous</a>`;
        paginationContainer.appendChild(prevLi);

        // 페이지 번호
        for (let i = 1; i <= totalPages; i++) {
            const li = document.createElement("li");
            li.className = "page-item" + (i === currentPage ? " active" : "");
            li.innerHTML = `<a class="page-link" href="#" data-page="${i}">${i}</a>`;
            paginationContainer.appendChild(li);
        }

        // Next
        const nextLi = document.createElement("li");
        nextLi.className = "page-item" + (currentPage === totalPages ? " disabled" : "");
        nextLi.innerHTML = `<a class="page-link" href="#" data-page="${currentPage + 1}">Next</a>`;
        paginationContainer.appendChild(nextLi);
    }

    // 페이지 클릭 + 카드 클릭
    document.addEventListener("click", (e) => {
        if (e.target.classList.contains("page-link")) {
            e.preventDefault();
            let pageNum = parseInt(e.target.dataset.page);
            if (isNaN(pageNum)) return;
            const totalPages = Math.ceil(boards.length / postsPerPage);
            if (pageNum < 1) pageNum = 1;
            if (pageNum > totalPages) pageNum = totalPages;
            currentPage = pageNum;

            renderBoardList();
            renderPagination();
            return;
        }

        const col = e.target.closest(".col");
        if (col && col.dataset.id) {
            window.location.href = `/board/boardDetailForm/${col.dataset.id}`;
        }
    });
});
