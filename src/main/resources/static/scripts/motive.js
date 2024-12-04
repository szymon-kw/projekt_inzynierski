const pageBody = document.getElementById("pageBody");
const checkbox = document.getElementById("checkbox");

checkbox.addEventListener("change", () =>{
    if (checkbox.checked){
        pageBody.setAttribute("data-bs-theme", "dark");
    }else{
        pageBody.setAttribute("data-bs-theme", "light");
    }
});