document.addEventListener("DOMContentLoaded", () => {
    const boardId = document.body.dataset.boardId;
    const likeContainer = document.querySelector(".mt-2");
    const likeCountSpan = document.getElementById("likeCount");

    const likeBtn = document.createElement("i");
    likeBtn.classList.add("bi"); // 기본 클래스만 넣기
    likeBtn.style.color = "red";
    likeBtn.style.cursor = "pointer";
    likeBtn.style.fontSize = "1.5rem";
    likeContainer.prepend(likeBtn);

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
});

