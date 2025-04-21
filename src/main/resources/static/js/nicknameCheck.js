const nicknameInput = document.getElementById("nicknameInput");
const nicknameCheck = document.getElementById("nicknameCheck");

nicknameInput.addEventListener("blur", function() {
    fetch("/api/nicknameInput", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ nickname: nicknameInput.value })
    })
    .then(response => response.json())
    .then(data => {
        if(data.nicknameExists) {
            nicknameCheck.style.display = 'block';
        } else {
            nicknameCheck.style.display = 'none';
        }
    })
    .catch(error => {
        alert("동일한 닉네임이 존재합니다.");
    })
})