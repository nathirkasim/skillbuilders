// user-cart-courses.js — Cart page logic

function fetchCartCourses() {
    const requestData = new URLSearchParams();
    requestData.append("type", "cart");
    // FIXED: no longer passing userid in body; server reads it from session

    fetch('/fetchusercourses', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: requestData.toString(),
    })
    .then(response => {
        if (response.status === 401) {
            window.location.href = 'user-login.html';
            return;
        }
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return response.json();
    })
    .then(responseData => {
        if (responseData) generateCourseHTML(responseData);
    })
    .catch(error => {
        console.error("Error fetching cart courses:", error);
    });
}

let courseIds = [];

function generateCourseHTML(courses) {
    const coursesContainer = document.querySelector('.courses-container');
    const checkoutBox = document.querySelector('.checkout-box');

    if (courses.length === 0) {
        coursesContainer.innerHTML = `
        <div class='no-course'>
          <h1>You haven't added any courses in the cart!</h1>
          <p>Explore courses using the search bar.</p>
        </div>`;
        checkoutBox.innerHTML = '';
        return;
    }

    coursesContainer.innerHTML = '';
    checkoutBox.innerHTML = '';
    courseIds = [];
    let totalPrice = 0;

    courses.forEach(course => {
        const { courseId, name, instructorName, price, rating, ratingCount, duration, moduleCount, enrolledCount, timeDate } = course;

        const courseHTML = `
      <div class='course-and-btns-container'>
        <div class="course-container">
          <div class="sb-gradient-card sb-gc-purple" style="width:160px;height:100px;border-radius:8px;flex-shrink:0"></div>
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
          <button class='remove-from-cart-btn btn' data-courseid="${courseId}">Remove from cart</button>
        </div>
      </div>`;

        courseIds.push(courseId);
        coursesContainer.innerHTML += courseHTML;
        checkoutBox.innerHTML += `
      <div class="checkout-item">
        <span class="checkout-course-name">${name}</span>:
        <span class="checkout-course-price">&#8377; ${price.toFixed(2)}</span>
      </div>`;
        totalPrice += price;
    });

    checkoutBox.innerHTML += `
    <div class="checkout-total"><span>Total:</span> <span class="checkout-total-price">&#8377; ${totalPrice.toFixed(2)}</span></div>
    <div class="checkout-buttons">
      <button class="checkout-button" onclick="redirectToPaymentPage()">Proceed to Payment</button>
    </div>`;

    // FIXED: attach event listeners after rendering (not inside loop)
    document.querySelectorAll('.remove-from-cart-btn').forEach(button => {
        button.addEventListener('click', event => {
            removeFromCart(parseInt(event.target.dataset.courseid));
        });
    });
}

function redirectCourse(courseId) {
    window.location.href = `show-course-preview.html?courseId=${courseId}`;
}

function removeFromCart(courseId) {
    fetch('/removefromcart', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        // FIXED: no longer sending userid; server uses session
        body: JSON.stringify({ courseid: courseId })
    })
    .then(res => res.json())
    .then(() => window.location.reload())
    .catch(error => console.error('Error:', error));
}

function redirectToPaymentPage() {
    const courseIdsString = courseIds.join(',');
    window.location.href = `transaction-page.html?courseids=${courseIdsString}`;
}

// Init
fetchCartCourses();
