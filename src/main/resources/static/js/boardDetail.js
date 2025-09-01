document.addEventListener("DOMContentLoaded", () => {
    // URL에서 게시글 ID 가져오기
    const urlParts = window.location.pathname.split("/");
    console.log(urlParts);
    const boardId = urlParts[urlParts.length - 1];
    console.log(boardId);

    fetch(`/board/api/board/${boardId}`)
        .then(res => res.json())
        .then(board => {
            document.getElementById("title").textContent = board.title;
            document.getElementById("content").textContent = board.content;
            document.getElementById("createTimeAgo").textContent = board.createTimeAgo;
            document.getElementById("likeCount").textContent = board.likeCount;
            
            const picturesDiv = document.getElementById("pictures");
            picturesDiv.innerHTML = "";
            board.pictures.forEach(pictures => {
                const img = document.createElement("img");
                img.src = pictures || 'images/default.jpg';
                img.style.width = "200px";
                img.style.marginRight = "10px";
                picturesDiv.appendChild(img);
            });
        })
        .catch(err => console.error(err));
});