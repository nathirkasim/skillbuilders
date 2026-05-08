// user-favourite-courses.js — Favourites page

function fetchFavouriteCourses() {
    const requestData = new URLSearchParams();
    requestData.append("type", "favourite");
    // FIXED: userid removed from body; server reads from session

    fetch('/fetchusercourses', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: requestData.toString(),
    })
    .then(response => {
        if (response.status === 401) { window.location.href = 'user-login.html'; return; }
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
    })
    .then(courses => { if (courses) generateCourseHTML(courses); })
    .catch(error => console.error("Error fetching favourites:", error));
}

function generateCourseHTML(courses) {
    const coursesContainer = document.querySelector('.courses-container');

    if (courses.length === 0) {
        coursesContainer.innerHTML = `<div class='no-course'><h1>You haven't added any courses to favourites!</h1><p>Explore courses using the search bar.</p></div>`;
        return;
    }

    let html = '';
    courses.forEach(course => {
        const { courseId, name, instructorName, price, rating, ratingCount, duration, moduleCount, enrolledCount } = course;
        html += `
      <div class='course-and-btns-container'>
        <div class="course-container">
          <div class="sb-gradient-card sb-gc-rose" style="width:160px;height:100px;border-radius:8px;flex-shrink:0"></div>
          <div class="course-details-container" onclick="redirectCourse(${courseId})" style="cursor:pointer">
            <div class="course-title">${name}</div>
            <div class="course-instructor">Instructor: ${instructorName || 'Unknown'}</div>
            <div class="enrolled-count">${enrolledCount}+ enrollments</div>
            <div class="course-rating">
              <div class="rating">${(ratingCount > 0 ? (rating / ratingCount).toFixed(1) : '0.0')}</div>
              <img class="ratings-star-image" src="images/rating-images/rating-star-image.png" alt="Rating Star">
              <div class="rating-count">(${ratingCount}+ ratings)</div>
            </div>
            <div class="additional-details">${duration} hours · ${moduleCount} modules</div>
          </div>
          <div class="course-price-container">&#8377; ${price.toFixed(2)}</div>
        </div>
        <div class='btns-container'>
          <button class='remove-from-favourite-btn btn' data-courseid="${courseId}">Remove from favourites</button>
          <button class='move-to-cart-btn btn' data-courseid="${courseId}">Move to cart</button>
        </div>
      </div>`;
    });

    coursesContainer.innerHTML = html;

    // FIXED: attach listeners after rendering (not inside forEach loop)
    document.querySelectorAll('.remove-from-favourite-btn').forEach(button => {
        button.addEventListener('click', e => removeFromFavourite(parseInt(e.target.dataset.courseid)));
    });
    document.querySelectorAll('.move-to-cart-btn').forEach(button => {
        button.addEventListener('click', e => moveToCart(parseInt(e.target.dataset.courseid)));
    });
}

function redirectCourse(courseId) {
    window.location.href = `show-course-preview.html?courseId=${courseId}`;
}

function removeFromFavourite(courseId) {
    fetch('/removefromfavourites', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        // FIXED: no longer sending userid; server uses session
        body: JSON.stringify({ courseid: courseId })
    })
    .then(res => res.json())
    .then(() => window.location.reload())
    .catch(error => console.error('Error:', error));
}

// FIXED: was calling both remove and add fetch() in parallel without chaining
// Now properly awaits remove before adding to cart
async function moveToCart(courseId) {
    try {
        await fetch('/removefromfavourites', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ courseid: courseId })
        });

        const addRes = await fetch('/addtocart', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ courseid: courseId })
        });
        const addData = await addRes.json();
        if (addData.status === 'success') {
            window.location.reload();
        } else {
            alert(addData.message || 'Could not move to cart.');
        }
    } catch (error) {
        console.error('Error moving to cart:', error);
    }
}

fetchFavouriteCourses();
