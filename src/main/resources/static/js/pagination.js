let currentPage = 1;   // 초기 페이지
let totalPages = 1;    // 서버에서 가져온 값으로 업데이트 예정

// 페이지네이션 렌더링 함수
function renderPagination(totalPages, currentPage) {
    const pagination = document.querySelector(".pagination");
    pagination.innerHTML = "";

    // Previous 버튼
    const prev = document.createElement("li");
    prev.classList.add("page-item");
    prev.innerHTML = `<a class="page-link" href="#">Previous</a>`;
    pagination.appendChild(prev);

    // 페이지 번호
    for (let i = 1; i <= totalPages; i++) {
        const li = document.createElement("li");
        li.classList.add("page-item");
        if (i === currentPage) li.classList.add("active");
        li.innerHTML = `<a class="page-link" href="#">${i}</a>`;
        pagination.appendChild(li);
    }
    // Next 버튼
    const next = document.createElement("li");
    next.classList.add("page-item");
    next.innerHTML = `<a class="page-link" href="#">Next</a>`;
    pagination.appendChild(next);
}

// 페이지 클릭 이벤트
document.querySelector(".pagination").addEventListener("click", async (e) => {
    if(e.target.classList.contains("page-link")) {
        e.preventDefault();
        const page = e.target.textContent.trim();
        let pageNum;

        if(page === "Previous") {
            pageNum = currentPage - 1;
        } else if(page === "Next") {
            pageNum = currentPage + 1;
        } else {
            pageNum = parseInt(page);
        }

        // 범위 체크
        if(pageNum < 1) pageNum = 1;
        if(pageNum > totalPages) pageNum = totalPages;

        // 현재 페이지 업데이트
        currentPage = pageNum;

        try {
            const response = await fetch(`/board/travelDestination?page=${pageNum}&size=10`);
            const data = await response.json();

            // 게시글 리스트 갱신
            const boardListContainer = document.getElementById("board-list"); // ✅ 통일
            boardListContainer.innerHTML = "";

            data.boards.forEach(board => {
                const div = document.createElement("div");
                div.textContent = board.title + " - " + board.createTimeAgo;
                boardListContainer.appendChild(div);
            });

            // 페이지네이션 다시 그리기
            totalPages = data.totalPages;
            renderPagination(totalPages, currentPage);

        } catch(err) {
            console.log("게시물 불러오기 실패", err);
        }
    }
});

// 최초 실행 시 1페이지 로딩
(async function init() {
    try {
        const response = await fetch(`/board/travelDestination?page=1&size=10`);
        const data = await response.json();

        currentPage = data.currentPage + 1; // Spring PageRequest는 0부터 시작
        totalPages = data.totalPages;

        // 게시글 렌더링
        const boardListContainer = document.getElementById("board-list"); // ✅ 통일
        boardListContainer.innerHTML = "";
        data.boards.forEach(board => {
            const div = document.createElement("div");
            div.textContent = board.title + " - " + board.createTimeAgo;
            boardListContainer.appendChild(div);
        });

        renderPagination(totalPages, currentPage);
    } catch(err) {
        console.log("Error fetching boards:", err);
    }
})();
