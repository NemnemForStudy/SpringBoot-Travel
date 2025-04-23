document.getElementById("loginBtn").addEventListener("click", function() {
    debugger;
    const email = document.getElementById("emailInput").value;
    const password = document.getElementById("passwordInput").value;

    if(!email || !password) {
        alert("이메일과 비밀번호를 입력해주세요.");
        return;
    }

    fetch("/api/login", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email: email, password: password})
    })
    .then(response => response.json())
    .then(data => {
        if(data.success) {
            alert("로그인 성공!");
            window.location.href = "/";
        } else {
            alert("이메일, 비밀번호를 확인해주세요.");
        }
    })
    .catch(error => {
        alert("로그인 중 에러 발생");
    })
})  