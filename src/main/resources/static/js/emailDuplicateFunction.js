// 전체 흐름
// 1. js에서 입력 후 중복 확인 누르면 fetch로 요청을 보냄.
// 2. UserApiController에서 중복 여부 확인.
// 3. 이미 존재하는 메일이면 "이미 존재하는 이메일" 메시지 띄움.

document.getElementById("checkEmailBtn").addEventListener("click", function () {
    // input값이어서 value 사용 가능.
    const email = document.getElementById("emailInput").value;

    // div라서 .value가 통하지 않음.
    const validMsg = document.getElementById("emailValid");
    const invaildMsg = document.getElementById("emailInValid");

    // 이메일 입력값 확인
    if (!email) {
        alert("이메일을 입력하세요.");
        return;
    }

    // @가 들어가있지 않으면 alert
    if(!email.includes('@')) {
        alert("이메일 형식이 올바르지 않습니다. @를 넣은 이메일이어야 합니다.");
        return;
    }

    // fetch 요청 전에 이메일이 비어있지 않다면 확인
    console.log("이메일 확인 중: ", email); // 디버깅을 위한 로그

    // fetch 요청
    // `/api/joinMembership?email=${encodeURIComponent(email)}`로 중복 여부 확인하는 GET 요청 보냄.
    // response.text()로 HTML을 반환하거나 텍스트를 반환.
    // available을 반환해서 있다면 if문 안에게 실행.
    fetch(`/api/joinMembership?email=${encodeURIComponent(email)}`)
        .then(response => response.text()) // JSON 대신 텍스트로 확인
        .then(data => {
            console.log("응답 내용:", data); // HTML일 경우 HTML이 출력될 것
            // 이 부분에서 data가 'available'을 포함하는 JSON 형식이 아니라면
            // HTML 페이지나 다른 오류 메시지일 수 있습니다.
            if (data.includes("available")) { // 실제 로직에 맞게 수정
                validMsg.style.display = "block";
                invaildMsg.style.display = "none";
            } else {
                validMsg.style.display = "none";
                invaildMsg.style.display = "block";
            }
        })
        .catch(error => {
            console.error("에러 발생:", error);
            alert("이메일 중복 확인 중 문제가 발생했습니다.");
        });
});
