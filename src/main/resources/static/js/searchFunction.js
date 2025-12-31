// URL에서 특정 파라미터(쿼리) 값을 가져오는 함수
function getQueryParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

// 검색 결과를 화면에 렌더링하는 함수
// 검색 결과를 화면에 렌더링하는 함수
function renderSearchResults(boards) {
    const boardList = document.getElementById("board-list");
    boardList.innerHTML = "";

    // 게시글이 없으면 "검색 결과 없음" 메시지 표시
    if (boards.length === 0) {
        boardList.innerHTML = `
            <div class="col-12 py-5 text-center bg-white rounded-3 shadow-sm">
                <i class="bi bi-search fs-1 text-muted mb-3 d-block"></i>
                <p class="text-muted">검색 결과가 없습니다.</p>
            </div>`;
        return;
    }

    boards.forEach(board => {
        const col = document.createElement("div");
        // 넓은 화면에서 사진이 커지는걸 막기 위해 col-12로 한 줄에 하나씩 나오게 고정합니다.
        col.className = "col-12 mb-3";

        const commentCount = board.commentList ? board.commentList.length : 0;

        // 핵심: CSS에서 정의한 클래스명(board-card, card-content 등)으로 교체
        col.innerHTML = `
            <div class="board-card">
                <div class="image-box">
                    <img src="${board.pictures && board.pictures.length > 0 ? board.pictures[0] : '/images/default.jpg'}" alt="여행지 이미지">
                </div>
                <div class="card-content">
                    <h5 class="card-title">${board.title}</h5>
                    <p class="card-excerpt">${board.content}</p>
                    <div class="card-meta">
                        <span><i class="bi bi-clock me-1"></i>${board.createTimeAgo}</span>
                        <span><i class="bi bi-heart-fill text-danger me-1"></i>${board.likeCount}</span>
                        <span><i class="bi bi-chat-dots me-1"></i>${commentCount}</span>
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

// 검색 기능 초기화 함수
// function initializeSearch() {
//     const query = getQueryParam('query');
    
//     // 서버에서 모든 게시글 데이터를 가져옵니다.
//     // 이 부분은 실제 API 엔드포인트에 맞게 수정해야 합니다.
//     fetch('/board/api/allBoards')
//         .then(response => {
//             if (!response.ok) {
//                 throw new Error('네트워크 응답이 올바르지 않습니다.');
//             }
//             return response.json();
//         })
//         .then(allBoards => {
//             let filteredBoards = allBoards;

//             if (query) {
//                 const lowerCaseQuery = query.toLowerCase();
//                 filteredBoards = allBoards.filter(board => {
//                     return board.title.toLowerCase().includes(lowerCaseQuery);
//                 });
//             }

//             // 필터링된 결과를 렌더링 함수에 전달
//             renderSearchResults(filteredBoards);
//         })
//         .catch(error => {
//             console.error('게시글을 가져오는 중 오류 발생:', error);
//             const boardList = document.getElementById("board-list");
//             boardList.innerHTML = `<p class="text-center text-danger">게시글을 불러올 수 없습니다. 다시 시도해 주세요.</p>`;
//         });
// }

// 페이지가 로드되면 검색 초기화 함수를 실행합니다.
// document.addEventListener('DOMContentLoaded', initializeSearch);