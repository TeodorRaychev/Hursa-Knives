let toastElList = document.querySelectorAll('.toast')
let toastCloseElList = document.querySelectorAll('.toast .toast-close')
for (const toastElListElement of toastElList) {
    sleep(10000).then(() => {
        toastElListElement.classList.remove('show')
        toastElListElement.classList.add('hide')
    })
}

for (const toastCloseElListElement of toastCloseElList) {
    toastCloseElListElement.addEventListener('click', () => {
        toastCloseElListElement.closest('.toast').classList.remove('show')
        toastCloseElListElement.closest('.toast').classList.add('hide')
    })
}

async function sleep(ms) {
    await new Promise(resolve => setTimeout(resolve, ms));
}
