let currentPage = 1;
let totalPages = 1;
const postsPerPage = 8;

// 게시글 렌더링 (가로 카드 + 클릭 이동 + default 이미지 처리)
function renderBoards(boards) {
    const boardListContainer = document.getElementById("board-list");
    boardListContainer.innerHTML = "";

    boards.forEach(board => {
        const col = document.createElement("div");
        col.classList.add("col");
        col.dataset.id = board.id; // 클릭 시 boardId 사용

        const card = document.createElement("div");
        card.classList.add("card", "h-100");
        card.style.display = "flex";
        card.style.flexDirection = "row";
        card.style.alignItems = "flex-start";
        card.style.gap = "20px";

        // 이미지
        const imgDiv = document.createElement("div");
        imgDiv.classList.add("image-box");
        const img = document.createElement("img");
        const isDefaultImage = !(board.pictures && board.pictures.length > 0);
        img.src = isDefaultImage ? "/Image/default.png" : board.pictures[0];
        imgDiv.appendChild(img);

        // 카드 본문
        const cardBody = document.createElement("div");
        cardBody.classList.add("card-body");
        cardBody.style.flex = "1";

        if (!isDefaultImage) { // default 이미지면 제목/내용 숨김
            const title = document.createElement("h5");
            title.classList.add("card-title");
            title.textContent = board.title;

            const content = document.createElement("p");
            content.classList.add("card-text");
            content.textContent = board.content;

            const info = document.createElement("p");
            info.classList.add("card-text", "text-muted");
            info.innerHTML = `작성일: ${board.createTimeAgo} <i class="bi bi-heart"></i> ${board.likeCount}`;

            cardBody.append(title, content, info);
        }

        card.append(imgDiv, cardBody);
        col.appendChild(card);
        boardListContainer.appendChild(col);
    });
}

// 페이지네이션 렌더링 (Previous / Next 포함)
function renderPagination() {
    const pagination = document.querySelector(".pagination");
    pagination.innerHTML = "";

    // Previous
    const prevLi = document.createElement("li");
    prevLi.className = "page-item" + (currentPage === 1 ? " disabled" : "");
    prevLi.innerHTML = `<a class="page-link" href="#" data-page="${currentPage - 1}">Previous</a>`;
    pagination.appendChild(prevLi);

    // 페이지 번호
    for (let i = 1; i <= totalPages; i++) {
        const li = document.createElement("li");
        li.className = "page-item" + (i === currentPage ? " active" : "");
        li.innerHTML = `<a class="page-link" href="#" data-page="${i}">${i}</a>`;
        pagination.appendChild(li);
    }

    // Next
    const nextLi = document.createElement("li");
    nextLi.className = "page-item" + (currentPage === totalPages ? " disabled" : "");
    nextLi.innerHTML = `<a class="page-link" href="#" data-page="${currentPage + 1}">Next</a>`;
    pagination.appendChild(nextLi);
}

// 페이지 클릭 이벤트 + board 클릭 이동 (이벤트 위임)
document.addEventListener("click", async (e) => {
    // 페이지네이션 클릭
    if (e.target.classList.contains("page-link")) {
        e.preventDefault();
        let pageNum = parseInt(e.target.dataset.page);
        if (isNaN(pageNum)) return;
        if (pageNum < 1) pageNum = 1;
        if (pageNum > totalPages) pageNum = totalPages;
        currentPage = pageNum;

        try {
            const response = await fetch(`/board/travelDestination?page=${currentPage}&size=${postsPerPage}`);
            const data = await response.json();
            totalPages = data.totalPages || 1;

            renderBoards(data.boards);
            renderPagination();
        } catch (err) {
            console.error("게시물 불러오기 실패", err);
        }
    }

    // 카드 클릭 -> detailForm 이동
    const col = e.target.closest(".col");
    if (col && col.dataset.id) {
        window.location.href = `/board/boardDetailForm/${col.dataset.id}`;
    }
});

// 초기 로딩
(async function init() {
    try {
        const response = await fetch(`/board/travelDestination?page=1&size=${postsPerPage}`);
        const data = await response.json();

        currentPage = data.currentPage || 1;
        totalPages = data.totalPages || 1;

        renderBoards(data.boards);
        renderPagination();
    } catch (err) {
        console.error("Error fetching boards:", err);
    }
})();
