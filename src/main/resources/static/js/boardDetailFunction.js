let currentUserEmail = "";
document.addEventListener("DOMContentLoaded", () => {
    const boardId = document.body.dataset.boardId;
    const likeCountSpan = document.getElementById("likeCount");
    const commentCountSpan = document.getElementById("commentCount");

    let liked = false;
    let likeCount = 0;

    const threeDotsBtn = document.getElementById("menuBtn");
    const deleteBtn = document.getElementById("deleteBtn");

    // 서버에서 초기 상태 불러오기
    fetch(`/api/likes/${boardId}/status`)
        .then(res => res.json())
        .then(data => {
            liked = data.liked;
            likeCount = data.likeCount;
            likeCountSpan.textContent = likeCount;

            commentCountSpan.textContent = data.commentCount;
            
            currentUserEmail = data.currentUserEmail;
            authorEmail = data.authorEmail;
            // 하트 상태 초기화
            if(liked) {
                likeBtn.classList.add("bi-heart-fill");
            } else {
                likeBtn.classList.add("bi-heart");
            }

            if(currentUserEmail !== authorEmail) {
                threeDotsBtn.style.display = 'none';    
            } else {
                threeDotsBtn.style.display = 'block';
            }
        })
        .catch(err => console.error("초기 상태 불러오기 실패: ", err));

    // 클릭 시 좋아요 토글
    likeBtn.addEventListener("click", () => {
        fetch(`/api/likes/${boardId}/like`, { method: "POST" })
            .then(res => res.json())
            .then(data => {
                liked = data.liked;
                likeCount = data.likeCount;
                likeCountSpan.textContent = likeCount;

            // 하트 업데이트
            if(liked) {
                likeBtn.classList.replace("bi-heart", "bi-heart-fill");
            } else {
                likeBtn.classList.replace("bi-heart-fill", "bi-heart");
            }
        })
        .catch(err => console.error("서버 저장 실패 : ", err));
    });

    // 삭제 버튼
    if(deleteBtn) {
        deleteBtn.addEventListener("click", async (e) => {
            e.stopPropagation(); // 삭제 버튼 클릭 시 카드 이벤트가 걸리지 않도록
            if(!confirm("정말 삭제하시겠습니까?")) return;

            try {
                const response = await fetch(`/board/${boardId}`, {
                    method : 'DELETE'
                });

                if(!response.ok) throw new Exception("삭제 실패");

                alert("삭제되었습니다.");
                window.location.href = "/travelDestination"; // 목록 페이지로 이동
            } catch(err) {
                alert("삭제에 실패했습니다.");
            }
        })
    }

    const submitBtn = document.getElementById("submitComment");
    const commentArea = document.getElementById("comment");

    if(submitBtn) {
        submitBtn.addEventListener("click", (e) => {
            e.preventDefault(); // 혹시 form submit 막기

            const content = commentArea.value.trim();

            if(content === "") {
                alert("댓글을 입력해주세요!");
                return;
            } 

            fetch(`/board/${boardId}/comment`, {
                method : 'POST',
                headers: { "Content-Type" : "application/json" },
                body: JSON.stringify({
                    boardId: boardId,
                    content: content
                })
            })
            .then(res => res.json())
            .then(data => {
                alert("댓글 등록에 성공했습니다.");
                window.location.reload();
            })
            .catch(err => console.error("댓글 등록 실패", err));
        });
    }

    const commentList = document.getElementById("commentList");

    // 댓글 하나를 HTML로 만들어서 추가하는 함수
    function addCommentToList(comment, currentUserEmail) {
        debugger;
        const div = document.createElement("div");
        div.classList.add("border", "p-2", "mb-2", "rounded", "position-relative");
    
        let threeDotsHTML = '';
        if(currentUserEmail === comment.email) {
            threeDotsHTML = `
                <div class="dropdown position-absolute" style="top:5px; right:5px;">
                    <i class="bi bi-three-dots-vertical" id="commentMenuBtn${comment.id}" 
                       style="cursor:pointer;" data-bs-toggle="dropdown"></i>
                    <ul class="dropdown-menu" aria-labelledby="commentMenuBtn${comment.id}">
                        <li><a class="dropdown-item edit-comment" href="#" data-id="${comment.id}">수정</a></li>
                        <li><a class="dropdown-item delete-comment" href="#" data-id="${comment.id}">삭제</a></li>
                    </ul>
                </div>
            `;
        }
    
        div.innerHTML = `
            <strong>${comment.author}</strong> 
            <span class="text-muted" style="font-size:0.8rem;">${comment.createTimeAgo}</span>
            <p>${comment.content}</p>
            ${threeDotsHTML}
        `;
        commentList.prepend(div);
    }

    fetch(`/board/${boardId}/comments`)
    .then(res => res.json())
    .then(data => {
        data.forEach(comment => addCommentToList(comment, currentUserEmail));
    })
    .catch(err => console.error("댓글 불러오기 실패", err));

    document.addEventListener("click", async (e) => {
        // 삭제 버튼 클릭 감지
        if(e.target.classList.contains("delete-comment")) {
            e.preventDefault();

            const commentId = e.target.dataset.id;
            if(!confirm("정말 삭제하시겠습니까?")) return;

            try {
                const resposne = await fetch(`/board/comment/${commentId}`, {
                    method : "DELETE"
                });

                if(!resposne.ok) throw new Exception("삭제 실패");

                alert("삭제되었습니다!");

                const commentDiv = e.target.closest("div.border");
                if(commentDiv) commentDiv.remove();
                commentCountSpan.textContent = parseInt(commentCountSpan.textContent) - 1;
            } catch(err) {
                alert("댓글 삭제에 실패했습니다.");
            }
        } 
    })
});
