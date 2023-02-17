window.addEventListener("DOMContentLoaded", (event) => {
  /**
   * Items page
   */
  if (document.getElementById('items-filter')) {
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
  }

  /**
   * Item create page
   */
  if (document.getElementById('item-create-form')) {
    document.getElementById('category-id').addEventListener('change', (e) => {
      document.getElementById('prefix').value = e.target.options[e.target.selectedIndex].dataset?.prefix ?? '';
    });
  }

  /**
   * Movements Page
   */
  if (document.getElementById('movements-filter')) {
    const element1 = document.querySelector('#locations-from');
    const choices1 = new Choices(element1, {
      removeItemButton: true,
      placeholderValue: "Откуда...",
      noResultsText: "Совпадений не найдено",
      itemSelectText: "",
    });
    const element2 = document.querySelector('#locations-to');
    const choices2 = new Choices(element2, {
      removeItemButton: true,
      placeholderValue: "Куда...",
      noResultsText: "Совпадений не найдено",
      itemSelectText: "",
    });
  }
});
