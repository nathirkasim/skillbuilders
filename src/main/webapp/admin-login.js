document.addEventListener("DOMContentLoaded", function () {
  document.getElementById("loginForm").addEventListener("submit", function (event) {
    event.preventDefault();

    const email = event.target.email.value.trim();
    const password = event.target.password.value.trim();
    const messageDiv = document.getElementById("formMessage");

    messageDiv.className = "sb-message";
    messageDiv.innerHTML = "";

    if (!email) { messageDiv.className = "sb-message error"; messageDiv.innerHTML = "Email cannot be empty."; return; }
    if (!password) { messageDiv.className = "sb-message error"; messageDiv.innerHTML = "Password cannot be empty."; return; }

    const formData = new URLSearchParams();
    formData.append("email", email);
    formData.append("password", password);

    fetch('/loginadmin', {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: formData.toString(),
    })
    .then(response => {
      if (!response.ok) throw new Error("Server error: " + response.status);
      return response.json();
    })
    .then(data => {
      if (data.result === "success") {
        window.location.href = "admin-handle.html";
      } else {
        messageDiv.className = "sb-message error";
        messageDiv.innerHTML = data.message;
      }
    })
    .catch(error => {
      messageDiv.className = "sb-message error";
      messageDiv.innerHTML = "An error occurred: " + error.message;
    });
  });
});
