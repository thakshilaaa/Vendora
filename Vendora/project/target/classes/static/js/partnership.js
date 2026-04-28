document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("completeRegistrationForm");

    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");

    const errorBox = document.getElementById("formError");

    // ===============================
    // GET TOKEN & EMAIL FROM URL
    // ===============================
    const params = new URLSearchParams(window.location.search);
    const token = params.get("token");
    const emailFromLink = params.get("email");

    // ===============================
    // BLOCK IF TOKEN OR EMAIL MISSING
    // ===============================
    if (!token || !emailFromLink) {
        showError("Invalid registration link.");
        form.style.display = "none";
        return;
    }

    // Auto-fill email & lock it
    emailInput.value = emailFromLink;
    emailInput.setAttribute("readonly", true);

    // ===============================
    // VALIDATION FUNCTIONS
    // ===============================
    function showError(message) {
        errorBox.innerText = message;
        errorBox.style.display = "block";
    }

    function clearError() {
        errorBox.innerText = "";
        errorBox.style.display = "none";
    }

    function validatePassword(password) {
        // Minimum 8 chars, at least 1 letter & 1 number
        const regex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,}$/;
        return regex.test(password);
    }

    function validateConfirmPassword(password, confirmPassword) {
        return password === confirmPassword;
    }

    // ===============================
    // REAL-TIME VALIDATION
    // ===============================
    passwordInput.addEventListener("input", () => {
        if (!validatePassword(passwordInput.value)) {
            passwordInput.classList.add("invalid");
        } else {
            passwordInput.classList.remove("invalid");
        }
    });

    confirmPasswordInput.addEventListener("input", () => {
        if (!validateConfirmPassword(passwordInput.value, confirmPasswordInput.value)) {
            confirmPasswordInput.classList.add("invalid");
        } else {
            confirmPasswordInput.classList.remove("invalid");
        }
    });

    // ===============================
    // FORM SUBMIT
    // ===============================
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        clearError();

        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();
        const confirmPassword = confirmPasswordInput.value.trim();

        // Validate password
        if (!validatePassword(password)) {
            showError("Password must be at least 8 characters and include letters and numbers.");
            return;
        }

        // Validate confirm password
        if (!validateConfirmPassword(password, confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            const response = await fetch("/api/auth/complete-registration", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    email: email,
                    password: password,
                    token: token
                })
            });

            const data = await response.json();

            if (!response.ok) {
                showError(data.message || "Registration failed.");
                return;
            }

            // SUCCESS
            alert("Registration successful! You can now log in.");
            window.location.href = "../../templates/epic1/auth/login.html";

        } catch (error) {
            console.error(error);
            showError("Something went wrong. Please try again.");
        }
    });

});