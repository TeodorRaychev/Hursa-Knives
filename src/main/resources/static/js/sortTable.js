const idSortBtn = document.getElementById("sortById");
const firstNameSortBtn = document.getElementById("sortByFirstName");
const lastNameSortBtn = document.getElementById("sortByLastName");
const emailSortBtn = document.getElementById("sortByEmail");
const rolesSortBtn = document.getElementById("sortByRoles");
idSortBtn.addEventListener("click", sort);
firstNameSortBtn.addEventListener("click", sort);
lastNameSortBtn.addEventListener("click", sort);
emailSortBtn.addEventListener("click", sort);
rolesSortBtn.addEventListener("click", sort);

function sort(event) {
    const target = event.target;
    const classList = target.classList;

    let tbody = document.getElementById("tbody");
    let rows = document.querySelectorAll("#tbody > tr");
    let prev_siblings = [];
    let prev_elem = target.previousElementSibling;
    while (prev_elem) {
        prev_siblings.push(prev_elem);
        prev_elem = prev_elem.previousElementSibling;
    }
    const columnNumber = prev_siblings.length;

    let sortedRows = Array.from(rows).sort((a, b) => {
        let valueA = a.children[columnNumber].textContent;
        let valueB = b.children[columnNumber].textContent;
        return valueA.localeCompare(valueB);
    });
    tbody.innerHTML = "";
    if (classList.contains("ASC")) {
        tbody.append(...sortedRows.reverse());
    } else {
        tbody.append(...sortedRows);
    }
    if (classList.contains("ASC")) {
        classList.remove("ASC");
        classList.add("DESC");
    } else {
        classList.remove("DESC");
        classList.add("ASC");
    }
}