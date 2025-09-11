// ===== 전역 변수 =====
const fileInput = document.getElementById("fileInput");
const fileSelectBtn = document.getElementById("fileSelectBtn");
const preview = document.getElementById("preview");
const form = document.getElementById("boardForm");
const fileDisplay = document.getElementById("fileDisplay");

let orderedFiles = []; // 선택한 파일 + 기존 파일 순서대로 관리
let deletedImages = []; // 기존 이미지 삭제 시 저장

// ===== 파일 선택 버튼 클릭 =====
fileSelectBtn.addEventListener("click", () => fileInput.click());

// ===== 파일 선택 처리 (순서 보존 불가: 기본 파일 선택창) =====
fileInput.addEventListener("change", function(event) {
    const files = Array.from(event.target.files);
    addFilesInOrder(files);
});

// ===== 드래그 앤 드롭 영역 추가 =====
const originalBorder = preview.style.border; 
preview.addEventListener("dragover", e => {
    e.preventDefault();
    preview.style.border = "2px dashed #007BFF";
});

preview.addEventListener("dragleave", () => {
    preview.style.border = originalBorder;
});

preview.addEventListener("drop", e => {
    e.preventDefault();
    preview.style.border = "originalBorder";
    const files = Array.from(e.dataTransfer.files);
    addFilesInOrder(files); // 드래그 순서 그대로 들어옴
});

// ===== 파일 추가 공통 처리 =====
function addFilesInOrder(files) {
    files.forEach(file => {
        const reader = new FileReader();
        reader.onload = function(e) {
            const img = new Image();
            img.src = e.target.result;

            img.onload = function() {
                EXIF.getData(img, function() {
                    let latitude = null, longitude = null;
                    const lat = EXIF.getTag(this, "GPSLatitude");
                    const lon = EXIF.getTag(this, "GPSLongitude");
                    const latRef = EXIF.getTag(this, "GPSLatitudeRef") || "N";
                    const lonRef = EXIF.getTag(this, "GPSLongitudeRef") || "E";

                    if (lat && lon) {
                        latitude = convertDMSToDD(lat[0], lat[1], lat[2], latRef);
                        longitude = convertDMSToDD(lon[0], lon[1], lon[2], lonRef);
                    }

                    orderedFiles.push({ file, isExisting: false, latitude, longitude });
                    updatePreview();
                });
            };
        };
        reader.readAsDataURL(file);
    });
}

// ===== 위도, 경도 변환 =====
function convertDMSToDD(degrees, minutes, seconds, direction) {
    let dd = degrees + minutes / 60 + seconds / 3600;
    if (direction === "S" || direction === "W") dd *= -1;
    return dd;
}

// ===== 미리보기 =====
function updatePreview() {
    preview.innerHTML = "";

    orderedFiles.forEach((item, index) => {
        const container = document.createElement("div");
        container.style.position = "relative";
        container.style.display = "inline-block";
        container.style.marginRight = "5px";
        container.style.marginBottom = "5px";

        const img = document.createElement("img");
        img.style.width = "80px";
        img.style.height = "80px";
        img.style.objectFit = "cover";

        if (item.isExisting) {
            img.src = item.src;
        } else {
            const reader = new FileReader();
            reader.onload = e => img.src = e.target.result;
            reader.readAsDataURL(item.file);
        }

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

        removeBtn.addEventListener("click", () => {
            const removed = orderedFiles.splice(index, 1)[0];
            if (removed.isExisting) deletedImages.push(removed.src);
            updatePreview();
        });

        container.appendChild(img);
        container.appendChild(removeBtn);
        preview.appendChild(container);
    });

    if (orderedFiles.length === 0) {
        fileDisplay.textContent = "파일이 선택되지 않았습니다.";
    } else {
        fileDisplay.textContent = `${orderedFiles.length} pictures`;
    }
}

// ===== 폼 제출 처리 (순서 그대로 서버 전송) =====
form.addEventListener("submit", function(event) {
    event.preventDefault();
    const formData = new FormData(form);

    deletedImages.forEach(url => formData.append("deletedImages", url));

    orderedFiles.forEach(item => {
        if (!item.isExisting) formData.append("files", item.file);
        formData.append("latitude", item.latitude ?? "");
        formData.append("longitude", item.longitude ?? "");
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
