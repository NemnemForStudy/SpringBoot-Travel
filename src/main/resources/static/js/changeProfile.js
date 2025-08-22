// 수정 아이콘 클릭 시 파일 선택창 열기.
document.getElementById('editIcon').addEventListener('click', () => {
    document.getElementById('profileUpload').click();
});

// 파일이 선택되었을 때 이벤트 리스너
document.getElementById('profileUpload').addEventListener('change', function(event) {
    const file = this.files[0];  // 선택된 파일
    // 파일이 없으면 반환
    if (!file) return;

    // FileReader 객체 생성
    // 1. 이미지 미리보기
    const reader = new FileReader();
    reader.onload = function(e) {
        // 읽어들인 데이터 URL로 이미지를 삽입
        const profilePreview = document.getElementById("profilePreview");
        profilePreview.innerHTML = `<img src="${e.target.result}" class="rounded-circle" style="width: 100%; height: 100%; object-fit: cover;">`;
    }
    reader.readAsDataURL(file);

    // 2. 서버로 전송.
    const formData = new FormData();
    formData.append('file', file);

    fetch('/api/profileUpload', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) throw new Error("Upload Failed");
        return response.text();
    })
    .then(result => {
        console.log('업로드 성공 : ', result);
        alert('프로필 이미지가 업로드 되었습니다!');
    })
    .catch(err => {
        console.error('업로드 실패:', err);
        alert("이미지 업로드 중 오류 발생");
    })
});

// 페이지 로딩 시 기존 프로필 이미지 가져오기
window.addEventListener('DOMContentLoaded', () => {
    fetch('/api/profileImg')
        .then(response => response.text())  // 서버에서 반환한 이미지를 텍스트로 받음
        .then(imgUrl => {
            if (imgUrl) {
                document.getElementById("profilePreview").innerHTML =
                    `<img src="${imgUrl}" class="rounded-circle" style="width: 100%; height: 100%; object-fit: cover;">`;
            }
        })
        .catch(err => {
            console.error('프로필 이미지 로딩 실패:', err);
        });
});
