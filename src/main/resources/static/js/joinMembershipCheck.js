function validateForm(event) {
    const email = document.querySelector('[name="email"]').value;
    const password = document.querySelector('[name="password"]').value;
    const passwordCheck = document.querySelector('[name="passwordCheck"]').value;
    const nickname = document.querySelector('[name="nickname"]').value;
    const phoneNumber = document.querySelector('[name="phoneNumber"]').value;
    const year = document.querySelector('[name="birthYear"]').value;
    const month = document.querySelector('[name="birthMonth"]').value;
    const day = document.querySelector('[name="birthDay"]').value;
    const emailCode = document.querySelector('[name=emailCode]').value;

    // 이메일 유효성 검사
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!emailPattern.test(email)) {
        alert("이메일 형식이 올바르지 않습니다.");
        return false;
    }

    if(!emailCode) {
        alert("이메일 인증을 해주세요.");
        return false;
    }

    // 비밀번호 길이 확인
    if (password.length < 8) {
        alert("비밀번호는 8자 이상이어야 합니다.");
        return false;
    }

    // 비밀번호 확인 일치 여부
    if (password !== passwordCheck) {
        alert("비밀번호가 일치하지 않습니다.");
        return false;
    }

    // 닉네임 공백 확인
    if (!nickname.trim()) {
        alert("닉네임을 입력하세요.");
        return false;
    }

    // 전화번호 형식 확인
    const phonePattern = /^\d{3}-\d{4}-\d{4}$/;
    if (!phonePattern.test(phoneNumber)) {
        alert("전화번호 형식이 올바르지 않습니다. 예: 010-1111-1111");
        return false;
    }

    // 생년월일 확인
    if (!year || !month || !day) {
        alert("생년월일을 올바르게 입력하세요.");
        return false;
    }

    alert("회원가입이 완료되었습니다!");
    window.location.href = "/";
    return false;
}