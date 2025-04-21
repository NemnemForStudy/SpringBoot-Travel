const phoneNumberInput = document.getElementById("phoneNumber");
const phoneErrorMsg = document.getElementById("phoneNumberCheck");

phoneNumberInput.addEventListener("blur", function() {
    const phoneNumber = phoneNumberInput.value;

    fetch("/api/checkPhoneNumber", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ phoneNumber: phoneNumber })
    })
    .then(response => response.json())
    .then(data => {
        console.log("data:", data);
        if (!data.valid) {
            phoneErrorMsg.style.display = 'block';
        } else {
            phoneErrorMsg.style.display = 'none';
        }
    })
    .catch(error => {
        console.error("전화번호 오류:", error);
        alert("전화번호 양식을 확인해주세요.");
    });
});
