// user-enrolled-courses.js — Enrolled courses page

function fetchEnrolledCourses() {
    const requestData = new URLSearchParams();
    requestData.append("type", "enrolled");
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
    .then(responseData => { if (responseData) generateCourseHTML(responseData); })
    .catch(error => console.error("Error fetching enrolled courses:", error));
}

function generateCourseHTML(courses) {
    const coursesContainer = document.querySelector('.courses-container');

    if (courses.length === 0) {
        coursesContainer.innerHTML = `<div class='no-course'><h1>You haven't enrolled in any courses!</h1><p>Explore courses using the search bar.</p></div>`;
        return;
    }

    let html = '';
    courses.forEach(course => {
        const { courseId, name, instructorName, price, rating, ratingCount, duration, moduleCount, enrolledCount } = course;
        html += `
      <div class='course-and-btns-container'>
        <div class="course-container">
          <div class="sb-gradient-card sb-gc-indigo" style="width:160px;height:100px;border-radius:8px;flex-shrink:0"></div>
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
          <button class='start-course-btn btn' data-courseid="${courseId}">Start course</button>
        </div>
      </div>`;
    });

    coursesContainer.innerHTML = html;

    document.querySelectorAll('.start-course-btn').forEach(button => {
        button.addEventListener('click', event => {
            window.location.href = `course-modules.html?courseId=${event.target.dataset.courseid}`;
        });
    });
}

function redirectCourse(courseId) {
    window.location.href = `show-course-preview.html?courseId=${courseId}`;
}

fetchEnrolledCourses();
