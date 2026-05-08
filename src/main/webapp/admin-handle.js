document.addEventListener("DOMContentLoaded", function () {
  fetch('/previewcourses')
    .then(r => r.json())
    .then(data => console.log("Admin loaded", data))
    .catch(e => console.error("Admin fetch error", e));
});
