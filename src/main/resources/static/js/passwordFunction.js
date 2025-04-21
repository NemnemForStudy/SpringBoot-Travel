const pwInput = document.getElementById("passwordInput");
const pwCheck = document.getElementById("passwordCheck");
const msgTrue = document.getElementById("passwordMismatchMsgTrue");
const msgFalse = document.getElementById("passwordMismatchMsgFalse");

function checkPasswordMatch() {
    if(pwInput.value && pwCheck.value) {
        if(pwInput.value === pwCheck.value) {
            msgTrue.style.display = 'block';
            msgFalse.style.display = 'none';
        } else {
            msgTrue.style.display = 'none';
            msgFalse.style.display = 'block';
        }
    } else {
        msgFalse.style.display = 'block';
    }
}

pwInput.addEventListener('input', checkPasswordMatch);
pwCheck.addEventListener('input', checkPasswordMatch);