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
    const travelTimeDisplay = document.getElementById("travelTime"); // 사진 아래에 시간 표시용 div

    let currentUserEmail = "";
    let liked = false;
    let likeCount = 0;
    let authorEmail = "";

    // ---------------------------
    // 초기 상태 불러오기
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

        likeBtn.classList.remove("bi-heart", "bi-heart-fill");
        likeBtn.classList.add(liked ? "bi-heart-fill" : "bi-heart");
        threeDotsBtn.style.display = currentUserEmail === authorEmail ? 'block' : 'none';

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
    // 댓글 렌더링 함수
    // ---------------------------
    function addCommentToList(comment, currentUserEmail) {
        const div = document.createElement("div");
        div.classList.add("border", "p-2", "mb-2", "rounded", "position-relative");

        let threeDotsHTML = "";
        console.log("comment : ", comment);
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
    // 댓글 수정/삭제 이벤트 위임
    // ---------------------------
    document.addEventListener("click", async (e) => {
        const commentDiv = e.target.closest("div.border");
        if (!commentDiv) return;

        // 삭제
        if (e.target.classList.contains("delete-comment")) {
            e.preventDefault();
            const commentId = e.target.dataset.id;
            if (!confirm("정말 삭제하시겠습니까?")) return;
            try {
                const response = await fetch(`/board/comment/${commentId}`, { method: "DELETE" });
                if (!response.ok) throw new Error("삭제 실패");
                commentDiv.remove();
                commentCountSpan.textContent = parseInt(commentCountSpan.textContent) - 1;
            } catch (err) {
                alert("댓글 삭제에 실패했습니다.");
            }
        }

        // 수정
        if (e.target.classList.contains("edit-comment")) {
            e.preventDefault();
            const contentP = commentDiv.querySelector("p");
            if (commentDiv.querySelector("textarea")) return;
            const originalContent = contentP.textContent;
            contentP.innerHTML = `
                <textarea class="form-control mb-2 edit-textarea">${originalContent}</textarea>
                <button class="btn btn-sm btn-success save-edit" data-id="${e.target.dataset.id}">저장</button>
                <button class="btn btn-sm btn-secondary cancel-edit" data-id="${e.target.dataset.id}">취소</button>
            `;
        }

        // 저장
        if (e.target.classList.contains("save-edit")) {
            e.preventDefault();
            const textarea = commentDiv.querySelector(".edit-textarea");
            const newContent = textarea.value.trim();
            if (!newContent) return alert("내용을 입력하세요!");
            try {
                const res = await fetch(`/board/comment/${e.target.dataset.id}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ content: newContent })
                });
                if (!res.ok) throw new Error("수정 실패");
                commentDiv.querySelector("p").innerHTML = newContent;
            } catch (err) {
                alert("댓글 수정에 실패했습니다.");
            }
        }

        // 취소
        if (e.target.classList.contains("cancel-edit")) {
            e.preventDefault();
            const textarea = commentDiv.querySelector(".edit-textarea");
            commentDiv.querySelector("p").innerHTML = textarea ? textarea.value : "";
        }
    });

    // ---------------------------
    // 지도 초기화
    // ---------------------------
    async function initMap(board) {
        debugger;
        if (!board.pictureDtos || board.pictureDtos.length === 0) return;

        const map = new naver.maps.Map("map", {
            center: new naver.maps.LatLng(board.pictureDtos[0].latitude, board.pictureDtos[0].longitude),
            zoom: 5,
            zoomControl: true,
            zoomControlOptions: {
                style: naver.maps.ZoomControlStyle.SMALL,
                position: naver.maps.Position.TOP_RIGHT
            }
        });

        const markers = [];
        const bounds = new naver.maps.LatLngBounds(); // 전체 경로 bounds

        console.log("board : ", board);
        console.log("board.pictureDtos : ", board.pictureDtos);

        for (let i = 0; i < board.pictureDtos.length; i++) {
            
            const dto = board.pictureDtos[i];
            const marker = new naver.maps.Marker({
                position: new naver.maps.LatLng(dto.latitude, dto.longitude),
                map: map,
                title: `여행지 ${i + 1}`
            });

            naver.maps.Event.addListener(marker, "click", async () => {
                if (photoContainer && photo && board.pictures[i]) {
                    photo.src = board.pictures[i];
                    photoContainer.style.display = "flex";
                }

                if (i < board.pictureDtos.length - 1) {
                    const start = dto;
                    const goal = board.pictureDtos[i + 1];
                    try {
                        const dirRes = await fetch(
                            `/api/naver/direction?startLat=${start.latitude}&startLng=${start.longitude}&goalLat=${goal.latitude}&goalLng=${goal.longitude}`
                        );
                        const dirData = await dirRes.json();
                        if (dirData.route && dirData.route.traoptimal && dirData.route.traoptimal.length > 0) {
                            const durationSec = dirData.route.traoptimal[0].summary.duration;
                            const hours = Math.floor(durationSec / 3600);
                            const minutes = Math.floor((durationSec % 3600) / 60);
                        }
                    } catch (err) {
                        console.error("경로 정보 불러오기 실패:", err);
                    }
                }
            });

            markers.push(marker);
            bounds.extend(marker.getPosition()); // bounds에 마커 추가
        }

        // 모든 마커와 Polyline이 포함되도록 지도 범위 조정
        map.fitBounds(bounds);

        if (naver.maps.MarkerClusterer) {
            new naver.maps.MarkerClusterer({
                map: map,
                markers: markers,
                minClusterSize: 2,
                gridSize: 120
            });
        }

        // Polyline
        debugger;
        console.log("board : ", board);
        for (let i = 0; i < board.pictureDtos.length - 1; i++) {
            const start = board.pictureDtos[i];
            const goal = board.pictureDtos[i + 1];
        
            try {
                const dirRes = await fetch(
                    `/api/naver/direction?startLat=${start.latitude}&startLng=${start.longitude}&goalLat=${goal.latitude}&goalLng=${goal.longitude}`
                );
                const dirData = await dirRes.json();
                
                if (dirData.route && dirData.route.traoptimal && dirData.route.traoptimal.length > 0) {
                    const sections = dirData.route.traoptimal[0].section;
                    const speeds = sections.map(sec => sec.speed);
                    
                    let avgSpeed = 0;
                    if(speeds !== null) {
                        for(let j = 0; j < speeds.length; j++) {
                            avgSpeed += speeds[j];
                        }
                        avgSpeed /= speeds.length;
                    }
                    
                    avgSpeed = Math.round(parseFloat(avgSpeed).toFixed(1));
                    
                    const totalDistance = dirData.route.traoptimal[0].summary.distance; // 미터 단위
                    const totalDistanceKm = totalDistance / 1000; // km로 변환

                    // 이동 시간 = 거리 / 속도
                    const durationHours = totalDistanceKm / avgSpeed;
                    const hours = Math.floor(durationHours);
                    const minutes = Math.round((durationHours - hours) * 60);

                    // 경로(path) Polyline
                    const path = dirData.route.traoptimal[0].path
                        .filter(c => Array.isArray(c) && c.length === 2)
                        .map(c => new naver.maps.LatLng(c[1], c[0]));

                    if (path.length > 1) { // 최소 2개 이상이어야 Polyline 생성
                        new naver.maps.Polyline({
                            map: map,
                            path: path,
                            strokeColor: "#FF0000",
                            strokeOpacity: 0.8,
                            strokeWeight: 4
                        });

                        // 경로 중간 위치 확인
                        const midIndex = Math.floor(path.length / 2);
                        const midLatLng = path[midIndex];

                        if (midLatLng) { // midLatLng가 존재할 때만 InfoWindow 생성
                            new naver.maps.Marker({
                                position: midLatLng,
                                map: map,
                                icon: {
                                    content: `<div style="padding:5px; background:white; border:1px solid black;">
                                    ${hours > 0 ? `${hours}시간 ` : ""}${minutes}분</div>`    
                                }
                            });
                        }
                    }
                }
            } catch (err) {
                console.error("Polyline 생성 실패:", err);
            }
        }
    }

    // ---------------------------
    // 지도 데이터 불러오기
    // ---------------------------
    try {
        const res = await fetch(`/board/api/board/${boardId}`);
        const board = await res.json();
        if (window.naver && naver.maps) {
            initMap(board);
        } else {
            console.error("naver.maps 로드 실패");
        }
    } catch (err) {
        console.error("지도 데이터 불러오기 실패:", err);
    }
});
