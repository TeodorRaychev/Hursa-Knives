const allButtons = document.getElementsByTagName("button");

for (const button of allButtons) {
    if (button.type === "submit") {
        button.addEventListener('click', () => {
            button.closest('form').submit();
            button.setAttribute("disabled", "true");
        })
    }
}
