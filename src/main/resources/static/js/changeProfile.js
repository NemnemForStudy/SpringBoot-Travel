const fileInput = document.getElementById('profileUpload');
const profilePreview = document.getElementById('profilePreview');
const profileIcon = document.getElementById('profileIcon');

// 파일이 선택되었을 때 이벤트 리스너
fileInput.addEventListener('change', function() {
    const file = this.files[0];  // 선택된 파일

    // 파일이 없으면 반환
    if (!file) return;

    // FileReader 객체 생성
    const reader = new FileReader();
    reader.onload = function(e) {
        // 읽어들인 데이터 URL로 이미지를 삽입
        const img = document.createElement('img');
        img.src = e.target.result;  // 파일 읽은 결과

        // 기존 SVG 아이콘 제거
        profilePreview.innerHTML = "";

        // 새로운 이미지 삽입
        profilePreview.appendChild(img);
    };

    // 파일이 있을 때만 파일 읽기
    if (file) {
        reader.readAsDataURL(file);
    }
});
