var pasInput = document.getElementById("password");
var pasConfInput = document.getElementById("confirmPassword")

var samePass = document.getElementById("samePassword")
var PasOk = false;
var submitButton = document.getElementById("button");
var length = document.getElementById("length");
var lower = document.getElementById("lowercase");
var upper = document.getElementById("uppercase");
var number = document.getElementById("number");
var specialChar = document.getElementById("specialChar");

pasInput.onkeyup = function () {
    var intMatch = 0;

    var lowerCasePattern = /[a-z]/g;
    if (pasInput.value.match(lowerCasePattern)) {
        intMatch++;
        lower.classList.remove("invalid");
        lower.classList.add("valid");
    } else {
        lower.classList.remove("valid");
        lower.classList.add("invalid");
    }

    var uppeCasePattern = /[A-Z]/g;
    if (pasInput.value.match(uppeCasePattern)) {
        intMatch++;
        upper.classList.remove("invalid");
        upper.classList.add("valid");
    } else {
        upper.classList.remove("valid");
        upper.classList.add("invalid");
    }

    var numberPattern = /[0-9]/g;
    if (pasInput.value.match(numberPattern)) {
        intMatch++;
        number.classList.remove("invalid");
        number.classList.add("valid");
    } else {
        number.classList.remove("valid");
        number.classList.add("invalid");
    }

    var specialCharPattern = /[!@#$%^&*()_\+\-=\[\]{};':"\\|,.<>\/?]/g;
    if (pasInput.value.match(specialCharPattern)) {
        intMatch++;
        specialChar.classList.remove("invalid");
        specialChar.classList.add("valid");
    } else {
        specialChar.classList.remove("valid");
        specialChar.classList.add("invalid");
    }

    if (pasInput.value.length >= 8 && pasInput.value.length <= 30) {
        intMatch++;
        length.classList.remove("invalid");
        length.classList.add("valid");
    } else {
        length.classList.remove("valid");
        length.classList.add("invalid");
    }

    if (intMatch === 5) {
        PasOk = true;
    } else {
        PasOk = false;
    }
    if (pasInput.value === pasConfInput.value){
        samePass.style.display = "none"
        if(PasOk){
            submitButton.disabled = false;
        }
    } else {
        samePass.style.display = "block"
        submitButton.disabled = true;
    }
}
pasConfInput.onkeyup = function (){

    if (pasInput.value === pasConfInput.value && PasOk){
        samePass.style.display = "none"
        submitButton.disabled = false;
    }else {
        samePass.style.display = "block"
    }
}