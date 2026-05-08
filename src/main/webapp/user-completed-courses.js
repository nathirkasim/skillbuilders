// user-completed-courses.js — Completed courses page

async function fetchCompletedCourses() {
    const requestData = new URLSearchParams();
    requestData.append("type", "completed");
    // FIXED: userid removed from body; server reads from session

    try {
        const response = await fetch('/fetchusercourses', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: requestData.toString(),
        });

        if (response.status === 401) { window.location.href = 'user-login.html'; return; }
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const responseData = await response.json();
        generateCourseHTML(responseData);
    } catch (error) {
        console.error("Error fetching completed courses:", error);
        document.querySelector('.courses-container').innerHTML = '<h1>Error fetching courses. Please try again later.</h1>';
    }
}

function generateCourseHTML(courses) {
    const coursesContainer = document.querySelector('.courses-container');

    if (courses.length === 0) {
        coursesContainer.innerHTML = `<div class='no-course'><h1>You haven't completed any courses yet!</h1><p>Go and finish your enrolled courses.</p></div>`;
        return;
    }

    let coursesHTML = '';
    courses.forEach(course => {
        const { courseId, name, instructorName, price, rating, ratingCount, duration, moduleCount, enrolledCount } = course;
        coursesHTML += `
      <div class='course-and-btns-container'>
        <div class="course-container">
          <div class="sb-gradient-card sb-gc-teal" style="width:160px;height:100px;border-radius:8px;flex-shrink:0"></div>
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
          <button class='get-certificate-btn btn' data-courseid="${courseId}">Get Certificate</button>
        </div>
      </div>`;
    });

    coursesContainer.innerHTML = coursesHTML;

    document.querySelectorAll('.start-course-btn').forEach(button => {
        button.addEventListener('click', e => {
            window.location.href = `course-modules.html?courseId=${e.target.dataset.courseid}`;
        });
    });

    document.querySelectorAll('.get-certificate-btn').forEach(button => {
        button.addEventListener('click', e => {
            fetch('/updatecourseid', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ courseId: e.target.dataset.courseid }),
            })
            .then(res => res.json())
            .then(() => { window.location.href = 'display-result.html'; })
            .catch(error => console.error('Error updating course ID:', error));
        });
    });
}

function redirectCourse(courseId) {
    window.location.href = `show-course-preview.html?courseId=${courseId}`;
}

fetchCompletedCourses();
