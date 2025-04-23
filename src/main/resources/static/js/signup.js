const signupForm = document.getElementById("joinForm");

document.getElementById("signupBtn").addEventListener("click", function(event) {
    debugger;
    const email = emailInput.value;  // 이메일 값
    const code = emailCode.value;  // 인증 코드 값
    const password = document.getElementById("passwordInput").value;  // 비밀번호 값
    const nickname = document.getElementById("nicknameInput").value;  // 닉네임 값
    const phoneNumber = document.getElementById("phoneNumber").value;  // 전화번호 값
    const birthYear = document.querySelector("select[name=birthYear]").value;  // 생년월일 (연도)
    const birthMonth = document.querySelector("select[name=birthMonth]").value;  // 생년월일 (월)
    const birthDay = document.querySelector("select[name=birthDay]").value;  // 생년월일 (일)

    const userData = {
        email: email,
        code: code,
        password: password,
        nickname: nickname,
        phoneNumber: phoneNumber,
        birthYear: birthYear,
        birthMonth: birthMonth,
        birthDay: birthDay
    };

    fetch("/api/signup", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(userData)
    })
    .then(response => response.json())
    .then(data => {
        if(data.success) {
            alert("회원가입이 완료되었습니다!");
            window.location.href = "/";
        } else {
            alert(data.message || "인증 코드가 일치하지 않거나 오류 발생.");
        }
    })
    .catch(error => {
        alert("오류 발생");
    })
}) 