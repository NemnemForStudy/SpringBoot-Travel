document.addEventListener("DOMContentLoaded", async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const boardId = urlParams.get("boardId");
    const titleInput = document.getElementById("title");
    const contentInput = document.getElementById("content");
    const preview = document.getElementById("preview");
    const fileDisplay = document.getElementById("fileDisplay");

    // 수정 모드이면 기존 데이터 가져오기
    if(boardId) {
        try {
            const response = await fetch(`/board/api/board/${boardId}`);
            const board = await response.json();

            titleInput.value = board.title;
            contentInput.value = board.content;

            // 이미지 미리보기 + 파일 이름 표시
            preview.innerHTML = "";
            fileDisplay.textContent = "";
            if(board.pictures && board.pictures.length > 0) {
                board.pictures.forEach((url) => {
                    const img = document.createElement("img");
                    img.src = url;
                    img.style.width = "80px";
                    img.style.height = "80px";
                    img.style.objectFit = "cover";
                    img.style.marginRight = "5px";
                    preview.appendChild(img);

                    const nameSpan = document.createElement("span");
                    nameSpan.textContent = url.split("/").pop() + " ";
                    fileDisplay.appendChild(nameSpan);
                });
            } else {
                fileDisplay.textContent = "파일이 선택되지 않았습니다.";
            }

            // 드롭다운 생성 + 선택 값 적용
            const numDropdowns = board.pictures.length - 1;
            const selectedOptions = board.selectedDropdownOptions || [];
            createExtraMultiDropdowns(numDropdowns, selectedOptions);

        } catch(err) {
            console.error("게시글 로딩 실패:", err);
        }
    }
});

// 저장/수정 버튼
document.getElementById('saveBtn').addEventListener('click', async function(event) {
    event.preventDefault();

    const urlParams = new URLSearchParams(window.location.search);
    const boardId = urlParams.get("boardId");

    const title = document.getElementById('title').value;
    const content = document.getElementById('content').value;

    // 드롭다운 값 수집
    const container = document.getElementById('dropdownContainer');
    const dropdowns = container ? container.querySelectorAll("select") : [];
    let selectedOptions = [];
    dropdowns.forEach(dropdown => {
        if(dropdown.value !== "" && dropdown.selectedIndex !== 0) {
            selectedOptions.push(dropdown.value);
        }
    });

    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    selectedOptions.forEach(option => formData.append("selectedDropdownOptions", option));
    selectedFiles.forEach(file => formData.append("files", file));

    try {
        const url = boardId ? `/board/api/update/${boardId}` : `/board/write`;
        const method = boardId ? 'PUT' : 'POST';

        const response = await fetch(url, { method, body: formData });
        const data = await response.json();

        if(data.success) {
            alert(boardId ? '게시글이 수정되었습니다!' : '글이 등록되었습니다!');
            window.location.href = boardId ? `/board/boardDetailForm/${boardId}` : '/travelDestination';
        } else {
            alert('실패: ' + data.message);
        }
    } catch(err) {
        alert('에러 발생: ' + err.message);
    }
});
