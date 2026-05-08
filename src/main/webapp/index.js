const images = [
  'images/body-images/image-1.jpg',
  'images/body-images/image-2.jpg',
  'images/body-images/image-3.jpg'
];

let currentIndex = 0;

window.addEventListener('DOMContentLoaded', () => {
  const dynamicImage = document.querySelector('.dynamic-image');

  if (!dynamicImage) {
    console.error("Element with class 'dynamic-image' not found.");
    return;
  }

  setInterval(() => {
    currentIndex = (currentIndex + 1) % images.length;
    dynamicImage.src = images[currentIndex];
  }, 2000);
});
