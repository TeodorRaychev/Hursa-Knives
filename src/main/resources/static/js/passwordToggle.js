const password = document.getElementById("togglePassword");
const confirmPassword = document.getElementById("toggleConfirmPassword");

if (password != null) {
    password.addEventListener("click", togglePassword);
}
if (confirmPassword != null) {
    confirmPassword.addEventListener("click", togglePassword);
}

function togglePassword(e) {
    e.preventDefault();
    const targetElement = e.currentTarget;
    const input = targetElement.parentNode.parentNode.firstElementChild;
    const type = input.getAttribute("type");
    if (type === "password") {
        input.setAttribute("type", "text");
        targetElement.classList.remove("fa-eye-slash");
        targetElement.classList.add("fa-eye");
    } else {
        input.setAttribute("type", "password");
        targetElement.classList.remove("fa-eye");
        targetElement.classList.add("fa-eye-slash");
    }
}
