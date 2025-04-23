let emailVerified = false;
let timer = null;
let timerLeft = 180;

const sendCodeBtn = document.getElementById("sendCodeBtn");

// 이메일 전송 버튼
sendCodeBtn.addEventListener('click', function(){
    // 이메일 입력 값
    const email = document.getElementById('emailInput').value;

    // 코드 확인 버튼 상태인지 확인
    if(sendCodeBtn.textContent === "코드 확인") {
        const code = document.getElementById("emailCode").value;

        fetch("/api/verifyCode", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email, code: code})
        })
        .then(response => response.json())
        .then(isValid => {
            if(isValid) {
                alert("이메일 인증 완료!");
                clearInterval(timer);
                document.getElementById("timer").textContent = ""; // 타이머 표시 제거
                emailVerified = true;
            } else {
                alert("인증 코드가 일치하지 않습니다.");
            }
        });
    } else {

        // 이메일 비어있는지 확인
        if(!email) {
            alert("먼저 이메일을 입력해주세요.");
            return;
        }

        //인증 코드 전송
        fetch("/api/sendCode", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email })
        })
        .then(response => response.text())
        .then(data => {
            alert("인증 코드가 이메일로 전송되었습니다.");
            sendCodeBtn.textContent = "코드 확인";
            startTimer();
        });
    }
});

function startTimer() {
    timerLeft = 180;
    const timerDisplay = document.getElementById("timer");
    timerDisplay.textContent = formatTime(timerLeft);

    timer = setInterval(() => {
        timerLeft--;
        timerDisplay.textContent = formatTime(timerLeft);

        if(timerLeft <= 0) {
            clearInterval(timer);
            sendCodeBtn.textContent = "코드 전송";
            alert("인증 시간이 만료되었습니다. 다시 요청해주세요.");
            timerDisplay.textContent = "";
        }
    }, 1000);
}

function formatTime(seconds) {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}:${secs < 10 ? '0' : ''}${secs}`;
}