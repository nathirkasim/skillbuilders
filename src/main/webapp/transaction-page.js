function renderTransactionForm(type = 'debit') {
    const formContainer = document.getElementById('transaction-form');
    formContainer.innerHTML = '';

    if (type === 'debit' || type === 'credit') {
        formContainer.innerHTML = `
            <h2>${type === 'debit' ? 'Debit Card' : 'Credit Card'} Payment</h2>
            <form id="payment-form">
                <label for="card-number">Card Number:</label>
                <input type="text" id="card-number" name="card-number" maxlength="19" placeholder="1234 5678 9012 3456" required>
                <br><br>
                <label for="expiry">Expiry Date:</label>
                <input type="month" id="expiry" name="expiry" required>
                <br><br>
                <label for="cvv">CVV:</label>
                <input type="password" id="cvv" name="cvv" maxlength="4" placeholder="***" required>
                <br><br>
                <button type="button" class="submit-button">Pay</button>
            </form>
        `;
    } else if (type === 'upi') {
        formContainer.innerHTML = `
            <h2>UPI Payment</h2>
            <form id="payment-form">
                <label for="upi-id">UPI ID:</label>
                <input type="text" id="upi-id" name="upi-id" placeholder="yourname@upi" required>
                <br><br>
                <button type="button" class="submit-button">Pay</button>
            </form>
        `;
    }

    const payButton = document.querySelector('.submit-button');
    payButton.addEventListener('click', handlePayment);
}

function handlePayment() {
    const urlParams = new URLSearchParams(window.location.search);
    const courseIds = urlParams.get('courseids');

    if (!courseIds) {
        alert('No course IDs found in the URL.');
        return;
    }

    const formData = new URLSearchParams();
    formData.append('courseids', courseIds);

    // FIXED: was hardcoded to http://localhost:7910/Skillbuilders-2/addtoenrolled
    fetch('/addtoenrolled', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData,
    })
    .then(response => {
        if (!response.ok) throw new Error(`Server responded with status ${response.status}`);
        return response.json();
    })
    .then(data => {
        if (data.result === 'success') {
            alert('Enrollment successful! Redirecting to your courses.');
            window.location.href = 'user-enrolled-courses.html';
        } else {
            alert(data.message || 'Enrollment failed. Please try again.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to enroll in courses. Please try again later.');
    });
}

renderTransactionForm('debit');
