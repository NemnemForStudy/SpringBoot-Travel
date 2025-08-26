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
});

