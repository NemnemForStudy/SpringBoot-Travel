const fileInput = document.getElementById("formFileMultiple");
const fileSelectBtn = document.getElementById("fileSelectBtn");
const preview = document.getElementById("preview");
const fileDisplay = document.getElementById("fileDisplay");

// 현재 선택된 파일 배열
let selectedFiles = [];

// 버튼 클릭 → 숨겨진 파일 입력 클릭
fileSelectBtn.addEventListener("click", () => {
  fileInput.click();
});

// 파일 선택 시 처리
fileInput.addEventListener("change", function (event) {
    selectedFiles = Array.from(event.target.files);
    updatePreview();
    updateFileDisplay();
});

function updatePreview() {
    preview.innerHTML = "";

    selectedFiles.forEach((file, index) => {
        const reader = new FileReader();
        reader.onload = function(e) {
            const imgContainer = document.createElement("div");
            imgContainer.style.position = "relative";
            imgContainer.style.display = "inline-block";

            const img = document.createElement("img");
            img.src = e.target.result;
            img.style.width = "80px";
            img.style.height = "80px";
            img.style.objectFit = "cover";
            img.style.border = "1px solid #ccc";
            img.style.borderRadius = "4px";

            const removeBtn = document.createElement("div");
            removeBtn.innerHTML = "&times;";
            removeBtn.style.position = "absolute";
            removeBtn.style.top = "3px";
            removeBtn.style.right = "3px";
            removeBtn.style.width = "20px";
            removeBtn.style.height = "20px";
            removeBtn.style.borderRadius = "50%";
            removeBtn.style.background = "rgba(255, 0, 0, 0.6)";
            removeBtn.style.color = "white";
            removeBtn.style.display = "flex";
            removeBtn.style.justifyContent = "center";
            removeBtn.style.alignItems = "center";
            removeBtn.style.cursor = "pointer";
            removeBtn.style.fontSize = "14px";

            removeBtn.addEventListener("click", () => {
                selectedFiles.splice(index, 1);
                updatePreview();
                updateFileDisplay();
            });

            imgContainer.appendChild(img);
            imgContainer.appendChild(removeBtn);
            preview.appendChild(imgContainer);
        };
        reader.readAsDataURL(file);
    });
}

function updateFileDisplay() {
    if (selectedFiles.length === 0) {
        fileDisplay.textContent = "파일이 선택되지 않았습니다.";
    } else if (selectedFiles.length === 1) {
        fileDisplay.textContent = selectedFiles[0].name;
    } else {
        fileDisplay.textContent = `${selectedFiles.length} pictures`;
    }
}