// ===== 전역 변수 =====
const fileInput = document.getElementById("formFileMultiple");
const fileSelectBtn = document.getElementById("fileSelectBtn");
const preview = document.getElementById("preview");
const fileDisplay = document.getElementById("fileDisplay");

let selectedFiles = [];       // 새로 선택한 파일
let existingImages = [];      // 서버에서 불러온 기존 이미지
let deletedImages = [];       // 기존 이미지 중 삭제된 것
let selectedDropdownOptions = []; // 드롭다운 선택 값

// ===== DOMContentLoaded: 수정 모드 처리 =====
document.addEventListener("DOMContentLoaded", async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const boardId = urlParams.get("boardId");
    const titleInput = document.getElementById("title");
    const contentInput = document.getElementById("content");

    if(boardId) {
        try {
            const res = await fetch(`/board/api/board/${boardId}`);
            const board = await res.json();

            titleInput.value = board.title;
            contentInput.value = board.content;

            existingImages = board.pictures || [];
            updatePreview();

            // 드롭다운 생성 + 선택 값 적용
            const numDropdowns = existingImages.length - 1;
            const selectedOptions = board.selectedDropdownOptions || [];
            createExtraMultiDropdowns(numDropdowns, selectedOptions);

        } catch(err) {
            console.error("게시글 로딩 실패:", err);
        }
    }
});

// ===== 파일 선택 버튼 클릭 =====
fileSelectBtn.addEventListener("click", () => fileInput.click());

// ===== 파일 선택 처리 =====
fileInput.addEventListener("change", function(event) {
    selectedFiles = Array.from(event.target.files);
    updatePreview();
    updateFileDisplay();
});

// ===== 이미지 미리보기 =====
function updatePreview() {
    preview.innerHTML = "";

    // 기존 이미지 렌더링
    existingImages.forEach(url => appendImage(url, true));

    // 새로 선택한 파일 렌더링
    selectedFiles.forEach(file => {
        const reader = new FileReader();
        reader.onload = e => appendImage(e.target.result, false, file);
        reader.readAsDataURL(file);
    });

    updateFileDisplay();
}

// ===== 이미지 생성 + 삭제 버튼 =====
function appendImage(src, isExisting, fileObj = null) {
    const container = document.createElement("div");
    container.style.position = "relative";
    container.style.display = "inline-block";
    container.style.marginRight = "5px";

    const img = document.createElement("img");
    img.src = src;
    img.style.width = "80px";
    img.style.height = "80px";
    img.style.objectFit = "cover";

    const removeBtn = document.createElement("div");
    removeBtn.textContent = "×";
    removeBtn.style.position = "absolute";
    removeBtn.style.top = "0";
    removeBtn.style.right = "0";
    removeBtn.style.width = "20px";
    removeBtn.style.height = "20px";
    removeBtn.style.background = "rgba(255,0,0,0.6)";
    removeBtn.style.color = "#fff";
    removeBtn.style.display = "flex";
    removeBtn.style.justifyContent = "center";
    removeBtn.style.alignItems = "center";
    removeBtn.style.cursor = "pointer";
    removeBtn.style.fontSize = "14px";

    removeBtn.addEventListener("click", () => {
        container.remove();
        if(isExisting) {
            deletedImages.push(src);
            existingImages = existingImages.filter(url => url !== src);
        } else if(fileObj) {
            selectedFiles = selectedFiles.filter(f => f !== fileObj);
        }
        updateFileDisplay();
    });

    container.appendChild(img);
    container.appendChild(removeBtn);
    preview.appendChild(container);
}

// ===== 파일 이름 표시 및 드롭다운 생성 =====
function updateFileDisplay() {
    const totalImages = existingImages.length + selectedFiles.length;

    if(totalImages === 0) {
        fileDisplay.textContent = "파일이 선택되지 않았습니다.";
        removeAllDropdowns();
    } else if(totalImages === 1) {
        fileDisplay.textContent = existingImages[0]?.split("/").pop() || selectedFiles[0].name;
        removeAllDropdowns();
    } else {
        fileDisplay.textContent = `${totalImages} pictures`;
        createExtraMultiDropdowns(totalImages - 1, selectedDropdownOptions);
    }
}

// ===== 다중 드롭다운 생성 =====
function createExtraMultiDropdowns(count, preSelected = []) {
    removeAllDropdowns();
    selectedDropdownOptions = [];

    const container = document.createElement("div");
    container.id = "dropdownContainer";
    container.style.display = "flex";
    container.style.gap = "10px";
    container.style.marginTop = "5px";
    container.style.flexWrap = "wrap";

    for(let i = 0; i < count; i++) {
        const dropdown = document.createElement("select");
        dropdown.className = "form-select";
        dropdown.style.width = "120px";

        const defaultOption = document.createElement("option");
        defaultOption.textContent = "선택하세요";
        defaultOption.disabled = true;
        defaultOption.selected = true;
        dropdown.appendChild(defaultOption);

        const options = [
            { value: "Public transport", text: "대중교통" },
            { value: "Vehicle", text: "자동차, 택시" },
            { value: "Walking", text: "도보" },
            { value: "Cycle", text: "자전거" }
        ];

        options.forEach(opt => {
            const optionElem = document.createElement("option");
            optionElem.value = opt.value;
            optionElem.textContent = opt.text;
            dropdown.appendChild(optionElem);
        });

        if(preSelected[i]) {
            dropdown.value = preSelected[i];
            selectedDropdownOptions[i] = preSelected[i];
        } else {
            dropdown.value = "";
            selectedDropdownOptions[i] = null;
        }

        dropdown.addEventListener("change", () => {
            selectedDropdownOptions[i] = dropdown.value;
        });

        container.appendChild(dropdown);
    }

    fileDisplay.insertAdjacentElement("afterend", container);
}

// ===== 드롭다운 제거 =====
function removeAllDropdowns() {
    const container = document.getElementById("dropdownContainer");
    if(container) container.remove();
}

// ===== 폼 제출 처리 =====
const form = document.querySelector("form");
form.addEventListener("submit", function(event) {
    event.preventDefault();

    if(selectedDropdownOptions.some(val => val === null)) {
        alert("모든 드롭다운 옵션을 선택해주세요.");
        return;
    }

    const formData = new FormData(form);

    selectedFiles.forEach(file => formData.append("files", file));
    deletedImages.forEach(url => formData.append("deletedImages", url));
    selectedDropdownOptions.forEach((val, idx) => {
        formData.append(`selectedDropdownOptions[${idx}]`, val);
    });

    const urlParams = new URLSearchParams(window.location.search);
    const boardId = urlParams.get("boardId");
    const url = boardId ? `/board/api/update/${boardId}` : `/board/write`;
    const method = boardId ? 'PUT' : 'POST';

    fetch(url, { method, body: formData })
        .then(res => res.json())
        .then(data => {
            alert(boardId ? "게시글이 수정되었습니다!" : "글이 등록되었습니다!");
            window.location.href = boardId ? `/board/boardDetailForm/${boardId}` : '/travelDestination';
        })
        .catch(err => {
            console.error(err);
            alert("업로드 실패!");
        });
});
