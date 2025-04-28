document.getElementById("editBtn").addEventListener('click', function() {
    const nicknameValue = document.getElementById("nicknameValue");
    const nicknameInput = document.getElementById("nicknameInput");
    const saveBtn = document.getElementById("saveBtn");

    // 닉네임을 입력 필드에 설정, 입력 필드와 확인 버튼 보여줌.
    nicknameInput.value = nicknameValue.innerText;
    nicknameValue.style.display = "none";
    nicknameInput.style.display = "inline";
    saveBtn.style.display = "inline-block";

    // 수정 버튼 숨김.
    this.style.display = "none";
});

// 확인 버튼 클릭 시 동작
document.getElementById("saveBtn").addEventListener('click', function() {
    const nicknameValue = document.getElementById("nicknameValue");
    const nicknameInput = document.getElementById("nicknameInput");
    const nicknameDisplay = document.getElementById("nicknameDisplay");

    // 입력된 값으로 닉네임 업데이트
    nicknameValue.innerText = nicknameInput.value;
    nicknameValue.style.display = "inline";

    // 입력필드, 확인 버튼 숨기기
    nicknameInput.style.display = "none";
    this.style.display = "none";

    // 수정버튼 다시 보이기
    document.getElementById("editBtn").style.display = "inline-block";

    // 수정된 닉네임 서버에 보내는 로직

    document.getElementById("editBtn").addEventListener("click", () => {
        const newNickname = nicknameInput.value.trim();

        fetch('/api/updateNickname', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ nickname: newNickname })
        })
        .then(response => {
            if(!response.ok) throw new Error('서버 오류');
            return response.json();
        })
        .then(data => {
            if(data.success) {
                nicknameValue.textContent = newNickname;
                nicknameValue.style.display = 'inline-block';
                nicknameInput.style.display = 'none';
                editButton.style.display = 'inline-block';
                saveButton.style.display = 'none';
            } else {
                alert(data.message || '닉네임 변경 실패');
            }
        })
        .catch(error => {
            alert('에러 발생: ' + error.message);
        })
    })
});