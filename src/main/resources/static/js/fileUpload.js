

document.addEventListener("DOMContentLoaded", function() {
    let orderFiles = []; // 선택 순서 기록

    const fileInput = document.getElementById("formFileMultiple");
    const fileSelectBtn = document.getElementById("fileSelectBtn");
    const fileList = document.getElementById("fileList");
    const preview = document.getElementById("preview");

    fileSelectBtn.addEventListener("click", () => fileInput.click());

    // 파일 선택 시 처리
    fileInput.addEventListener("change", function(e) {
        let files = Array.from(e.target.files);

        // 새로 선택된 파일들을 순서대로 push
        files.forEach(file => {
            orderFiles.push(file);

            // 리스트에 표시
            let li = document.createElement("li");
            li.textContent = file.name;
            fileList.appendChild(li);

            // 미리보기
            const reader = new FileReader();
            reader.onload = function(event) {
                const img = document.createElement('img');
                img.src = event.target.result;
                img.style.width = "100px";
                img.style.height = "100px";
                img.style.objectFit = "cover";
                preview.appendChild(img);
            };

            reader.readAsDataURL(file);
        });

        const fileDisplay = document.getElementById("fileDisplay");
        fileDisplay.textContent = orderFiles.length > 0 
                                    ? `${orderFiles.length} 개의 파일이 선택되었습니다.`
                                    : "파일이 선택되지 않았습니다.";
    });
});