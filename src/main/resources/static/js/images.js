function change_image(image) {
    let container = document.getElementById("main-image");
    container.src = image.src;
}

document.addEventListener("DOMContentLoaded", function (event) {
});

function getPics() {
} //just for this demo
const img = document.getElementById('main-image');
const fullPage = document.getElementById('fullpage');

console.log(img);

img.addEventListener('click', function () {
    fullPage.style.backgroundImage = 'url(' + img.src + ')';
    fullPage.style.display = 'block';
});
getPics()