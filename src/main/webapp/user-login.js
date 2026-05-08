document.addEventListener("DOMContentLoaded", function () {
  document.getElementById("loginForm").addEventListener("submit", function (event) {
    event.preventDefault();

    const email = event.target.email.value.trim();
    const password = event.target.password.value.trim();
    const messageDiv = document.getElementById("formMessage");

    messageDiv.innerHTML = "";

    if (!email) { messageDiv.innerHTML = `<span style="color:red;">Email cannot be empty.</span>`; return; }
    if (!password) { messageDiv.innerHTML = `<span style="color:red;">Password cannot be empty.</span>`; return; }

    const formData = new URLSearchParams();
    formData.append("email", email);
    formData.append("password", password);

    fetch('/loginuser', {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: formData.toString(),
    })
    .then(response => {
      if (!response.ok) throw new Error(`Server responded with ${response.status}`);
      return response.json();
    })
    .then(data => {
      if (data.result === "success") {
        window.location.href = "user-index.html";
      } else {
        messageDiv.innerHTML = `<span style="color:red;">${data.message}</span>`;
      }
    })
    .catch(error => {
      messageDiv.innerHTML = `<span style="color:red;">An error occurred: ${error.message}</span>`;
    });
  });
});
