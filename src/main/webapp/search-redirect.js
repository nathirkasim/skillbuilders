// Search redirect helper — used on pages with a standalone search box
const searchInput = document.querySelector('.search-box');
const searchButton = document.querySelector('.search-button');

function redirectToSearchPage() {
    if (!searchInput) return;
    const query = searchInput.value.trim();
    if (query) {
        window.location.href = `search-courses.html?query=${encodeURIComponent(query)}`;
    } else {
        alert("Please enter a search term.");
    }
}

if (searchInput) {
    searchInput.addEventListener('keydown', function (event) {
        if (event.key === 'Enter') redirectToSearchPage();
    });
}

if (searchButton) {
    searchButton.addEventListener('click', function () {
        redirectToSearchPage();
    });
}
