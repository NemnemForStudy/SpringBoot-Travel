const nicknameValue = document.getElementById("nicknameValue");
const nicknameInput = document.getElementById("nicknameInput");
const editBtn = document.getElementById("editBtn");
const saveBtn = document.getElementById("saveBtn");

saveBtn.style.display = "none";

// 수정 버튼 클릭
editBtn.addEventListener('click', function() {
    nicknameInput.value = nicknameValue.innerText; // 기존 닉네임을 input에 넣기
    nicknameValue.style.display = "none";
    nicknameInput.style.display = "inline-block";
    editBtn.style.display = "none";
    saveBtn.style.display = "inline-block";
});

// 확인 버튼 클릭 시 동작
// 저장(확인) 버튼 클릭
saveBtn.addEventListener('click', function() {
    const newNickname = nicknameInput.value;
    console.log("newNickname : " + newNickname);

    // 서버로 닉네임 업데이트 요청
    fetch('/api/updateNickname', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ nickname: newNickname })
    })
    .then(response => {
        if (!response.ok) throw new Error('서버 오류');
        return response.json();
    })
    .then(data => {
        if (data.success) {
            // 성공했으면 화면에 반영
            debugger;
            nicknameValue.innerText = data.nickname;
            nicknameValue.style.display = "inline-block";
            nicknameInput.style.display = "none";
            editBtn.style.display = "inline-block";
            saveBtn.style.display = "none";

            fetch('/api/getUserNickname')
                .then(response => response.json())
                .then(data => {
                    if(data.success) {
                        console.log(data.nickname);
                        nicknameValue.innerText = data.nickname;
                    } else {
                        console.error('닉네임 가져오기 실패');
                    }
                })
                .catch(error => console.error('서버 오류', error));
        } else {
            alert(data.message || '닉네임 변경 실패');
        }
    })
    .catch(error => {
        alert('에러 발생: ' + error.message);
    });
});