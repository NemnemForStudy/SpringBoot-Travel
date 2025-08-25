document.getElementById('saveBtn').addEventListener('click', async function(event) {
    event.preventDefault();  // 폼 제출 막기
    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;

    // FormData 생성
    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);

    selectedFiles.forEach(file => {
        formData.append("files", file);
    });

    fetch('/board/write', { 
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) throw new Error('서버 오류 발생');
        return response.json();
    })
    .then(data => {
        console.log("서버 응답:", data);
        if (data.success) {
            alert('글이 성공적으로 등록되었습니다!');
            window.location.href = '/travelDestination';
        } else {
            alert('글 등록 실패: ' + data.message);
        }
    })
    .catch(error => {
        alert('에러 발생 : ' + error.message);
    });
});

document.addEventListener("DOMContentLoaded", async () => {
    debugger;
    const urlParams = new URLSearchParams(window.location.search);
    const boardId = urlParams.get("boardId");

    if(boardId) {
        try {
            const response = await fetch(`/api/board/${boardId}`);
            const board = await response.json();

            document.getElementById("title").value = board.title;
            document.getElementById("content").value = board.content;

        } catch(err) {
            console.error("게시글 로딩 실패:", err);
        }
    }
})