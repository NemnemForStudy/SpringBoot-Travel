const passwordInput = document.getElementById("passwordInput");
const passwordLengthCheck = document.getElementById("passwordLengthCheck");

passwordInput.addEventListener("blur", function() {
    fetch("/api/passwordInput", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ password: passwordInput.value }) // 비밀번호 값 전달
    })
    .then(response => response.json()) // 서버 응답 받기
    .then(data => {
        if(data.valid) {
            // 유효한 경우 (8자리 이상)
            passwordLengthCheck.style.display = 'none'; // 경고 메시지 숨기기
        } else {
            // 유효하지 않은 경우 (8자리 미만)
            passwordLengthCheck.style.display = 'block'; // 경고 메시지 보이기
        }
    })
    .catch(error => {
        console.error("오류:", error);
    });
});
