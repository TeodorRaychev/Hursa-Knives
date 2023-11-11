let toastElList = document.querySelectorAll('.toast')
for (const toastElListElement of toastElList) {
    sleep(10000).then(() => {
        toastElListElement.classList.remove('show')
        toastElListElement.classList.add('hide')
    })
}

async function sleep(ms) {
    await new Promise(resolve => setTimeout(resolve, ms));
}
