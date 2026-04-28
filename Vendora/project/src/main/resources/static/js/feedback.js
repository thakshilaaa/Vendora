document.addEventListener("DOMContentLoaded", () => {
    const feedbackGrid = document.getElementById("feedbackGrid");
    const feedbackPanel = document.getElementById("feedbackPanel");
    const openPanelBtn = document.getElementById("openPanelBtn");
    const cancelBtn = document.getElementById("cancelBtn");
    const feedbackForm = document.getElementById("feedbackForm");
    const filterButtons = document.querySelectorAll(".feedback-filters button");
    const loggedUserText = document.getElementById("loggedUser");
    const ratingInput = document.getElementById("rating");
    const messageInput = document.getElementById("message");
    feedbackPanel?.classList.remove("open");

    const currentUser = getCurrentUser();
    function getCurrentUser() {
        const storedUser = JSON.parse(localStorage.getItem("loggedInUser"));
        if (storedUser && storedUser.name && storedUser.role) {
            return {
                id: storedUser.id || `${storedUser.role}-${storedUser.name.toLowerCase().replace(/\s+/g, "-")}`,
                name: storedUser.name,
                role: normalizeRole(storedUser.role)
            };
        }
        return {
            id: "guest-user",
            name: "Guest",
            role: "guest"
        };
    }

    function normalizeRole(role) {
        const value = String(role || "").toLowerCase().trim();
        return ["customer", "supplier", "delivery", "guest", "admin"].includes(value) ? value : "guest";
    }

    function formatRole(role) {
        switch (role) {
            case "customer": return "Customer";
            case "supplier": return "Supplier";
            case "delivery": return "Delivery Person";
            case "admin": return "Admin";
            default: return "Guest";
        }
    }

    if (loggedUserText) {
        loggedUserText.textContent = `User: ${currentUser.name} (${formatRole(currentUser.role)})`;
    }

    let feedbacks = JSON.parse(localStorage.getItem("feedbacks")) || [];
    if (feedbacks.length === 0) {
        feedbacks = [
            {
                id: crypto.randomUUID(),
                userId: "customer-alice",
                name: "Alice",
                role: "customer",
                rating: 5,
                message: "Great website! Easy to use.",
                createdAt: "2026-03-28T09:20:00"
            },
            {
                id: crypto.randomUUID(),
                userId: "delivery-bob",
                name: "Bob",
                role: "delivery",
                rating: 4,
                message: "Fast delivery and good packaging.",
                createdAt: "2026-03-29T11:10:00"
            }
        ];
        saveFeedbacks();
    }

    function saveFeedbacks() {
        localStorage.setItem("feedbacks", JSON.stringify(feedbacks));
    }

    function renderFeedback(filter = "all") {
        if (!feedbackGrid) return;
        feedbackGrid.innerHTML = "";
        let data = feedbacks.filter(fb => {
            if (filter === "all") return true;
            if (filter === "routine") return fb.category === "routine";
            return fb.role === filter;
        });

        data = [...data].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

        if (!data.length) {
            feedbackGrid.innerHTML = `
                <div class="empty-feedback">
                    <i class="fas fa-comment-slash"></i>
                    <p>No feedback available.</p>
                </div>
            `;
            return;
        }

        data.forEach(fb => {
            const stars = "★".repeat(fb.rating) + "☆".repeat(5 - fb.rating);
            const canDelete = currentUser.role === "admin" || currentUser.id === fb.userId;
            const isRoutine = fb.category === "routine";
            const card = document.createElement("div");
            card.className = "feedback-card";
            card.dataset.role = fb.role;
            card.innerHTML = `
                <div class="feedback-top">
                    <div class="feedback-user-block">
                        <h4>${escapeHTML(fb.name)}</h4>
                        <div class="feedback-date">${formatDate(fb.createdAt)}</div>
                        <div class="feedback-role">${formatRole(fb.role)}</div>
                        ${isRoutine ? `<div class="routine-badge">Routine (${fb.routineType || "general"})</div>` : ""}
                    </div>
                </div>
                <div class="feedback-rating">${stars}</div>
                <div class="feedback-message">${escapeHTML(fb.message)}</div>
                ${canDelete ? `
                    <button class="delete-feedback-btn" data-id="${fb.id}">
                        <i class="fas fa-trash-alt"></i> Delete
                    </button>
                ` : ""}
            `;
            feedbackGrid.appendChild(card);
        });
        attachDeleteEvents();
    }

    function attachDeleteEvents() {
        document.querySelectorAll(".delete-feedback-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                const id = btn.dataset.id;
                const fb = feedbacks.find(f => f.id === id);
                if (!fb) return;
                const allowed = currentUser.role === "admin" || currentUser.id === fb.userId;
                if (!allowed) {
                    alert("You cannot delete this feedback.");
                    return;
                }
                if (!confirm("Delete this feedback?")) return;
                feedbacks = feedbacks.filter(f => f.id !== id);
                saveFeedbacks();
                const activeFilter = document.querySelector(".feedback-filters button.active")?.dataset.filter || "all";
                renderFeedback(activeFilter);
            });
        });
    }

    function formatDate(dateString) {
        const date = new Date(dateString);

        return date.toLocaleDateString("en-LK", {
            year: "numeric",
            month: "short",
            day: "numeric"
        }) + " • " + date.toLocaleTimeString("en-LK", {
            hour: "2-digit",
            minute: "2-digit"
        });
    }

    function escapeHTML(str) {
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;");
    }

    filterButtons.forEach(btn => {
        btn.addEventListener("click", () => {
            filterButtons.forEach(b => b.classList.remove("active"));
            btn.classList.add("active");
            renderFeedback(btn.dataset.filter);
        });
    });

    openPanelBtn?.addEventListener("click", () => {
        feedbackPanel?.classList.add("open");
    });

    cancelBtn?.addEventListener("click", () => {
        feedbackPanel?.classList.remove("open");
        feedbackForm?.reset();
    });

    feedbackForm?.addEventListener("submit", (e) => {
        e.preventDefault();

        const rating = parseInt(ratingInput.value || "0", 10);
        const message = messageInput.value.trim();
        if (rating < 1 || rating > 5) {
            alert("Select a valid rating.");
            return;
        }
        if (!message) {
            alert("Enter your feedback.");
            return;
        }
        const newFeedback = {
            id: crypto.randomUUID(),
            userId: currentUser.id,
            name: currentUser.name,
            role: currentUser.role === "admin" ? "guest" : currentUser.role,
            rating,
            message,
            createdAt: new Date().toISOString()
        };

        feedbacks.push(newFeedback);
        saveFeedbacks();
        feedbackForm.reset();
        feedbackPanel.classList.remove("open");
        alert("Feedback submitted successfully!");
        const activeFilter = document.querySelector(".feedback-filters button.active")?.dataset.filter || "all";
        renderFeedback(activeFilter);
    });
    renderFeedback();

});