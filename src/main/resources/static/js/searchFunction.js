document.addEventListener("DOMContentLoaded", () => {
    const boardList = document.getElementById("board-list");
    const paginationContainer = document.querySelector("ul.pagination");
    const searchForm = document.getElementById("searchForm");
    const searchInput = document.getElementById("searchInput");
    const postsPerPage = 8;

    let boards = [];
    let currentPage = 1;

    searchForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const query = searchInput.value.trim();
        if (!query) return;

        try {
            const res = await fetch(`/board/search?query=${encodeURIComponent(query)}`);
            boards = await res.json();
            currentPage = 1;
            renderBoardList();
            renderPagination();
        } catch (err) {
            console.error("검색 실패:", err);
            boardList.innerHTML = "<p>검색 중 오류가 발생했습니다.</p>";
        }
    });

    function renderBoardList() {
        boardList.innerHTML = "";

        if (boards.length === 0) {
            boardList.innerHTML = "<p>검색 결과가 없습니다.</p>";
            return;
        }

        const start = (currentPage - 1) * postsPerPage;
        const end = start + postsPerPage;
        const pageBoards = boards.slice(start, end);

        pageBoards.forEach(board => {
            const col = document.createElement("div");
            col.className = "col-12 col-md-6 col-lg-4 mb-3";

            const commentCount = board.commentList ? board.commentList.length : 0;

            col.innerHTML = `
                <div class="card h-100">
                    <div class="d-flex">
                        <div class="image-box me-3">
                            <img src="${board.pictures && board.pictures.length > 0 ? board.pictures[0] : '/images/default.jpg'}" class="card-img-top">
                        </div>
                        <div class="card-body p-0">
                            <h5 class="card-title">${board.title}</h5>
                            <p class="card-text">${board.content}</p>
                            <div class="d-flex">
                                <small class="text-muted">작성일: ${board.createTimeAgo}</small>
                                <i class="bi bi-heart-fill ms-2"></i>
                                <span class="ms-1 like-count">${board.likeCount}</span>
                                <i class="bi bi-chat-square ms-2"></i>
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

    function renderPagination() {
        paginationContainer.innerHTML = "";
        const totalPages = Math.ceil(boards.length / postsPerPage);

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

    // 페이지 클릭 이벤트
    paginationContainer.addEventListener("click", (e) => {
        if (!e.target.classList.contains("page-link")) return;
        e.preventDefault();

        let pageNum = parseInt(e.target.dataset.page);
        if (isNaN(pageNum)) return;

        const totalPages = Math.ceil(boards.length / postsPerPage);
        if (pageNum < 1) pageNum = 1;
        if (pageNum > totalPages) pageNum = totalPages;

        currentPage = pageNum;
        renderBoardList();
        renderPagination();
    });
});
