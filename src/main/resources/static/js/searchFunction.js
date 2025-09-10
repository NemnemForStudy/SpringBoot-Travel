// URL에서 특정 파라미터(쿼리) 값을 가져오는 함수
function getQueryParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}

// 검색 결과를 화면에 렌더링하는 함수
function renderSearchResults(boards) {
    const boardList = document.getElementById("board-list");
    boardList.innerHTML = "";

    // 게시글이 없으면 "검색 결과 없음" 메시지 표시
    if (boards.length === 0) {
        boardList.innerHTML = `<p class="text-center">검색 결과가 없습니다.</p>`;
        return;
    }

    boards.forEach(board => {
    console.log("board : ", board);
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