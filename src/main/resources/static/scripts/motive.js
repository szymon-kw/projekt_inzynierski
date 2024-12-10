const pageBody = document.getElementById("pageBody");
const checkbox = document.getElementById("checkbox");


document.addEventListener("DOMContentLoaded", () => {
  const savedTheme = localStorage.getItem("theme");
  if (savedTheme) {
    pageBody.setAttribute("data-bs-theme", savedTheme);
    checkbox.checked = savedTheme === "dark";
  }
});


checkbox.addEventListener("change", () => {
  const theme = checkbox.checked ? "dark" : "light";
  pageBody.setAttribute("data-bs-theme", theme);
  localStorage.setItem("theme", theme);
});
