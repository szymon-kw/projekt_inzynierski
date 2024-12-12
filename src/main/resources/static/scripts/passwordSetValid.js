const pasInput = document.getElementById("password");
const pasConfInput = document.getElementById("confirmPassword")

const samePass = document.getElementById("samePassword")
var PasOk = false;
const submitButton = document.getElementById("button");
const length = document.getElementById("length");
const lower = document.getElementById("lowercase");
const upper = document.getElementById("uppercase");
const number = document.getElementById("number");
const specialChar = document.getElementById("specialChar");

pasInput.onkeyup = function () {
    let intMatch = 0;

    const lowerCasePattern = /[a-z]/g;
    if (pasInput.value.match(lowerCasePattern)) {
        intMatch++;
        lower.classList.remove("d-block");
    } else {
        lower.classList.add("d-block");
    }

    const upperCasePattern = /[A-Z]/g;
    if (pasInput.value.match(upperCasePattern)) {
        intMatch++;
        upper.classList.remove("d-block");
    } else {
        upper.classList.add("d-block");
    }

    const numberPattern = /[0-9]/g;
    if (pasInput.value.match(numberPattern)) {
        intMatch++;
        number.classList.remove("d-block");
    } else {
        number.classList.add("d-block");
    }

    const specialCharPattern = /[!@#$%^&*()_\+\-=\[\]{};':"\\|,.<>\/?]/g;
    if (pasInput.value.match(specialCharPattern)) {
        intMatch++;
        specialChar.classList.remove("d-block");
    } else {
        specialChar.classList.add("d-block");
    }

    if (pasInput.value.length >= 8 && pasInput.value.length <= 30) {
        intMatch++;
        length.classList.remove("d-block");
    } else {
        length.classList.add("d-block");
    }

    if (intMatch === 5) {
        PasOk = true;
        pasInput.classList.add("is-valid");
        pasInput.classList.remove("is-invalid");
    } else {
        PasOk = false;
        pasInput.classList.add("is-invalid");
        pasInput.classList.remove("is-valid");
    }
    if (pasInput.value === pasConfInput.value){
        pasConfInput.classList.add("is-valid");
        pasConfInput.classList.remove("is-invalid");
        samePass.classList.remove("d-block");
        if(PasOk){
            submitButton.disabled = false;
        }
    } else {
        pasConfInput.classList.add("is-invalid");
        pasConfInput.classList.remove("is-valid");
        samePass.classList.add("d-block");
        submitButton.disabled = true;
    }
}
pasConfInput.onkeyup = function (){

    if (pasInput.value === pasConfInput.value && PasOk){
        pasConfInput.classList.add("is-valid");
        pasConfInput.classList.remove("is-invalid");
        samePass.classList.remove("d-block");
        submitButton.disabled = false;
    }else {
        pasConfInput.classList.add("is-invalid");
        pasConfInput.classList.remove("is-valid");
        samePass.classList.add("d-block");
        submitButton.disabled = true;
    }
}