window.addEventListener("DOMContentLoaded", (event) => {
    const element1 = document.querySelector('#locations');
    const choices1 = new Choices(element1, {
        removeItemButton: true,
        placeholderValue: "Подразделения",
        noResultsText: "Совпадений не найдено",
        itemSelectText: "",
    });
    const element2 = document.querySelector('#categories');
    const choices2 = new Choices(element2, {
        removeItemButton: true,
        placeholderValue: "Категории",
        noResultsText: "Совпадений не найдено",
        itemSelectText: "",
    });
});