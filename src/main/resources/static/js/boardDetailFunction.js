document.addEventListener("DOMContentLoaded", async () => {
    const boardId = document.body.dataset.boardId;
    const likeCountSpan = document.getElementById("likeCount");
    const commentCountSpan = document.getElementById("commentCount");
    const likeBtn = document.getElementById("likeBtn");
    const threeDotsBtn = document.getElementById("menuBtn");
    const deleteBtn = document.getElementById("deleteBtn");
    const submitBtn = document.getElementById("submitComment");
    const commentArea = document.getElementById("comment");
    const commentList = document.getElementById("commentList");
    const photoContainer = document.getElementById("photo-container");
    const photo = document.getElementById("photo");

    let currentUserEmail = "";
    let liked = false;
    let likeCount = 0;
    let authorEmail = "";

    // ---------------------------
    // 초기 상태 불러오기 (status → comments 순서 보장)
    // ---------------------------
    try {
        const statusRes = await fetch(`/board/api/board/${boardId}/status`);
        const statusData = await statusRes.json();

        currentUserEmail = statusData.currentUserEmail;
        authorEmail = statusData.authorEmail;
        liked = statusData.liked;
        likeCount = statusData.likeCount;
        likeCountSpan.textContent = likeCount;
        commentCountSpan.textContent = statusData.commentCount;

        // 하트 상태 초기화
        likeBtn.classList.remove("bi-heart", "bi-heart-fill");
        likeBtn.classList.add(liked ? "bi-heart-fill" : "bi-heart");

        // 게시글 작성자만 메뉴 보이기
        threeDotsBtn.style.display = currentUserEmail === authorEmail ? 'block' : 'none';

        // 댓글 불러오기
        const commentRes = await fetch(`/board/api/board/${boardId}/comments`);
        const comments = await commentRes.json();

        commentList.innerHTML = "";
        if (Array.isArray(comments)) {
            comments.forEach(comment => addCommentToList(comment, currentUserEmail));
        }
    } catch (err) {
        console.error("초기 상태 불러오기 실패:", err);
    }

    // ---------------------------
    // 좋아요 클릭
    // ---------------------------
    likeBtn.addEventListener("click", async () => {
        try {
            const res = await fetch(`/api/likes/${boardId}/like`, { method: "POST" });
            const data = await res.json();
            liked = data.liked;
            likeCount = data.likeCount;
            likeCountSpan.textContent = likeCount;

            likeBtn.classList.remove("bi-heart", "bi-heart-fill");
            likeBtn.classList.add(liked ? "bi-heart-fill" : "bi-heart");
        } catch (err) {
            console.error("서버 저장 실패:", err);
        }
    });

    // ---------------------------
    // 게시글 삭제
    // ---------------------------
    if (deleteBtn) {
        deleteBtn.addEventListener("click", async (e) => {
            e.stopPropagation();
            if (!confirm("정말 삭제하시겠습니까?")) return;
            try {
                const response = await fetch(`/board/${boardId}`, { method: "DELETE" });
                if (!response.ok) throw new Error("삭제 실패");
                alert("삭제되었습니다.");
                window.location.href = "/travelDestination";
            } catch (err) {
                alert("삭제에 실패했습니다.");
            }
        });
    }

    // ---------------------------
    // 댓글 등록
    // ---------------------------
    if (submitBtn) {
        submitBtn.addEventListener("click", async (e) => {
            e.preventDefault();
            const content = commentArea.value.trim();
            if (!content) return alert("댓글을 입력해주세요!");

            try {
                const res = await fetch(`/board/${boardId}/comment`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ boardId, content })
                });
                const comment = await res.json();

                addCommentToList(comment, currentUserEmail);
                commentArea.value = "";
                commentCountSpan.textContent = parseInt(commentCountSpan.textContent) + 1;
            } catch (err) {
                console.error("댓글 등록 실패:", err);
            }
        });
    }

    // ---------------------------
    // 댓글 렌더링
    // ---------------------------
    function addCommentToList(comment, currentUserEmail) {
        const div = document.createElement("div");
        div.classList.add("border", "p-2", "mb-2", "rounded", "position-relative");

        let threeDotsHTML = "";
        if (currentUserEmail === comment.email) {
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

    // ---------------------------
    // 댓글 삭제 이벤트 (위임)
    // ---------------------------
    document.addEventListener("click", async (e) => {
        if (e.target.classList.contains("delete-comment")) {
            e.preventDefault();
            const commentId = e.target.dataset.id;
            if (!confirm("정말 삭제하시겠습니까?")) return;

            try {
                const response = await fetch(`/board/comment/${commentId}`, { method: "DELETE" });
                if (!response.ok) throw new Error("삭제 실패");

                const commentDiv = e.target.closest("div.border");
                if (commentDiv) commentDiv.remove();
                commentCountSpan.textContent = parseInt(commentCountSpan.textContent) - 1;
            } catch (err) {
                alert("댓글 삭제에 실패했습니다.");
            }
        }
    });

    // ---------------------------
    // 지도 및 마커 + 경로
    // ---------------------------
    try {
        const res = await fetch(`/board/api/board/${boardId}`);
        const board = await res.json();

        if (!board.pictureDtos || board.pictureDtos.length === 0) return;

        const map = new naver.maps.Map("map", {
            center: new naver.maps.LatLng(board.pictureDtos[0].latitude, board.pictureDtos[0].longitude),
            zoom: 12,
            zoomControl: true,
            zoomControlOptions: {
                style: naver.maps.ZoomControlStyle.SMALL,
                position: naver.maps.Position.TOP_RIGHT
            }
        });

        // 마커 & 클릭 이벤트
        board.pictureDtos.forEach((dto, idx) => {
            const marker = new naver.maps.Marker({
                position: new naver.maps.LatLng(dto.latitude, dto.longitude),
                map: map,
                title: `여행지 ${idx + 1}`,
                clickable: true
            });

            naver.maps.Event.addListener(marker, "click", () => {
                if (photoContainer && photo && board.pictures[idx]) {
                    photo.src = board.pictures[idx];
                    photoContainer.style.display = "flex";
                }
            });
        });

        // 여러 경유지 경로 Polyline
        for (let i = 0; i < board.pictureDtos.length - 1; i++) {
            const start = board.pictureDtos[i];
            const goal = board.pictureDtos[i + 1];

            const dirRes = await fetch(
                `/api/naver/direction?startLat=${start.latitude}&startLng=${start.longitude}&goalLat=${goal.latitude}&goalLng=${goal.longitude}`
            );
            const dirData = await dirRes.json();

            if (dirData.route && dirData.route.traoptimal && dirData.route.traoptimal.length > 0) {
                const path = dirData.route.traoptimal[0].path
                    .filter(coord => Array.isArray(coord) && coord.length === 2)
                    .map(coord => new naver.maps.LatLng(coord[1], coord[0]));

                if (path.length > 0) {
                    new naver.maps.Polyline({
                        map: map,
                        path: path,
                        strokeColor: "#FF0000",
                        strokeOpacity: 0.8,
                        strokeWeight: 4
                    });
                }
            }
        }
    } catch (err) {
        console.error(err);
    }

    // 댓글 수정 이벤트(위임)
    document.addEventListener("click", async (e) => {
        // 수정 버튼 클릭
        if(e.target.classList.contains("edit-comment")) {
            e.preventDefault();
            const commentId = e.target.dataset.id;

            const commentDiv = e.target.closest("div.border");
            const contentP = commentDiv.querySelector("p");

            // 이미 수정 보드면 무시
            if(commentDiv.querySelector("textarea")) return;

            const originalContent = contentP.textContent;

            // textarea + 저장버튼 UI 변경
            contentP.innerHTML = `
            <textarea class="form-control mb-2 edit-textarea">${originalContent}</textarea>
            <button class="btn btn-sm btn-success save-edit" data-id="${commentId}">저장</button>
            <button class="btn btn-sm btn-secondary cancel-edit" data-id="${commentId}">취소</button>
        `;
        }

        // 저장 버튼 클릭
        if (e.target.classList.contains("save-edit")) {
            e.preventDefault();
            const commentId = e.target.dataset.id;
            const commentDiv = e.target.closest("div.border");
            const textarea = commentDiv.querySelector(".edit-textarea");
            const newContent = textarea.value.trim();

            if (!newContent) return alert("내용을 입력하세요!");

            try {
                const res = await fetch(`/board/comment/${commentId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ content: newContent })
                });

                if (!res.ok) throw new Error("수정 실패");

                // 성공 시 화면 갱신
                commentDiv.querySelector("p").innerHTML = newContent;
            } catch (err) {
                alert("댓글 수정에 실패했습니다.");
                console.error(err);
            }
        }

        // 취소 버튼 클릭
        if (e.target.classList.contains("cancel-edit")) {
            e.preventDefault();
            const commentId = e.target.dataset.id;
            const commentDiv = e.target.closest("div.border");
            const textarea = commentDiv.querySelector(".edit-textarea");
            const originalContent = textarea ? textarea.value : "";

            // textarea → 원래 내용 복원
            commentDiv.querySelector("p").innerHTML = originalContent;
        }
    })
});
