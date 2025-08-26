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
        removeAllDropdowns();
    } else if (selectedFiles.length === 1) {
        fileDisplay.textContent = selectedFiles[0].name;
        removeAllDropdowns();
    } else {
        fileDisplay.textContent = `${selectedFiles.length} pictures`;
        createExtraMultiDropdowns(selectedFiles.length - 1); // n-1개 생성
    }
}

// 전역 변수로 선언
let selectedDropdownOptions = [];

// 다중 드롭다운 생성 함수
function createExtraMultiDropdowns(count, preSelected = []) {
    removeAllDropdowns();

    selectedDropdownOptions = []; // 초기화

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

        // 옵션 추가
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

        // 서버에서 미리 선택된 값이 있다면 적용
        if(preSelected[i]) {
            dropdown.value = preSelected[i];
            selectedDropdownOptions[i] = preSelected[i];
        } else {
            dropdown.value = ""; // default
            selectedDropdownOptions[i] = null;
        }

        dropdown.addEventListener("change", () => {
            selectedDropdownOptions[i] = dropdown.value;
        });

        container.appendChild(dropdown);
    }

    // 기존 DOM 요소 fileDisplay 뒤에 새로 만든 dropdown을 붙여라는 의미.
    fileDisplay.insertAdjacentElement("afterend", container);
}

// 버튼 제거 함수
function removeAllDropdowns() {
    const container = document.getElementById("dropdownContainer");
    if (container) container.remove();
}

// 폼 제출 시, 사진 + 드롭다운 값 추가
const form = document.querySelector("form");

form.addEventListener("submit", function(event) {
    // 기본 제출 막고 FormData 처리
    event.preventDefault();

    // 선택되지 않은 드롭다운이 있으면 제출 막기
    if(selectedDropdownOptions.some(val => val === null)) {
        alert("모든 드롭다운 옵션을 선택해주세요.");
        return;
    }

    const formData = new FormData(form);

    // 선택된 사진 추가
    selectedFiles.forEach(file => formData.append("files", file));

    // 드롭다운 값 추가
    selectedDropdownOptions.forEach((val, idx) => {
        formData.append(`selectedDropdownOptions[$(idx)]`, val);
    });

    fetch(form.action, {
        method : "POST",
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        alert("업로드 완료!");
        window.location.href = "/travelDestination";
    })
    .catch(err => {
        console.error(err);
        alert("업로드 실패!");
    })
})