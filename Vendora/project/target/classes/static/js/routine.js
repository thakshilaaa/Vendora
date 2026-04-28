document.addEventListener("DOMContentLoaded", () => {

    if (!window.location.pathname.includes("routine.html") && !window.location.pathname.endsWith("/routine")) return;
    const params = new URLSearchParams(window.location.search);
    const category = params.get("category");
    const concernForm = document.getElementById("concernForm");
    const categoryTitle = document.getElementById("categoryTitle");
    const concernsContainer = document.getElementById("concernsContainer");
    const resultDiv = document.getElementById("result");
    const feedbackSection = document.getElementById("feedbackSection");

    if (!category || !concernForm || !categoryTitle || !concernsContainer || !resultDiv) return;

    categoryTitle.textContent = `Selected: ${category.toUpperCase()}`;

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

    const concernsData = {
        skincare: ["Oily/combination skin", "Acne & clogged pores", "Hyperpigmentation", "Dehydration", "Fungal infections", "Heat rashes"],
        haircare: ["Hair fall", "Dandruff", "Frizz", "Dry/damaged hair", "Oily scalp", "Split ends"],
        bodycare: ["Body acne", "Fungal infections", "Body odor", "Dark areas", "Dry skin", "Heat rashes"]
    };

    const routineTemplates = {
        skincare: {
            "Oily/combination skin": { symptoms: "Shiny forehead, nose, or chin; enlarged pores.", causes: "Overactive sebaceous glands, hormones, or diet.", routine: "Morning: Gentle cleanser + Oil-free moisturizer\nNight: Clay mask weekly + Light moisturizer" },
            "Acne & clogged pores": { symptoms: "Blackheads, whiteheads, red pimples.", causes: "Excess oil, bacteria, hormonal changes.", routine: "Morning: Salicylic acid cleanser + Non-comedogenic moisturizer\nNight: Spot treatment + Moisturizer" },
            "Hyperpigmentation": { symptoms: "Dark spots, uneven skin tone.", causes: "Sun exposure, acne scars, hormones.", routine: "Morning: Sunscreen + Vitamin C serum\nNight: Retinol or brightening serum" },
            "Dehydration": { symptoms: "Tight, flaky skin; dull appearance.", causes: "Low water intake, harsh products.", routine: "Morning: Hydrating cleanser + Moisturizer\nNight: Hydrating serum + Moisturizer" },
            "Fungal infections": { symptoms: "Red itchy patches.", causes: "Moisture or heat.", routine: "Keep area dry, antifungal creams" },
            "Heat rashes": { symptoms: "Red bumps, itching.", causes: "Blocked sweat ducts.", routine: "Cool compress, breathable clothing" }
        },
        haircare: {
            "Hair fall": { symptoms: "Excess shedding.", causes: "Stress, nutrition.", routine: "Mild shampoo, protein treatments" },
            "Dandruff": { symptoms: "Flaky scalp.", causes: "Dry scalp.", routine: "Anti-dandruff shampoo" },
            "Frizz": { symptoms: "Dry unruly hair.", causes: "Humidity.", routine: "Moisturizing shampoo + serum" },
            "Dry/damaged hair": { symptoms: "Brittle hair.", causes: "Heat styling.", routine: "Deep conditioning" },
            "Oily scalp": { symptoms: "Greasy roots.", causes: "Sebum.", routine: "Clarifying shampoo" },
            "Split ends": { symptoms: "Frayed tips.", causes: "Dryness.", routine: "Trim regularly" }
        },
        bodycare: {
            "Body acne": { symptoms: "Red bumps.", causes: "Clogged pores.", routine: "Exfoliating wash" },
            "Fungal infections": { symptoms: "Itchy patches.", causes: "Moisture.", routine: "Antifungal cream" },
            "Body odor": { symptoms: "Bad smell.", causes: "Bacteria.", routine: "Daily cleansing" },
            "Dark areas": { symptoms: "Pigmentation.", causes: "Friction.", routine: "Exfoliation + creams" },
            "Dry skin": { symptoms: "Flaky skin.", causes: "Low humidity.", routine: "Moisturizer" },
            "Heat rashes": { symptoms: "Red bumps.", causes: "Sweat.", routine: "Cool showers" }
        }
    };

    concernsContainer.innerHTML = "";
    concernsData[category]?.forEach(concern => {
        const label = document.createElement("label");
        label.innerHTML = `<input type="checkbox" value="${concern}"> ${concern}`;
        concernsContainer.appendChild(label);
    });

    const otherLabel = document.createElement("label");
    otherLabel.innerHTML = `<input type="checkbox" id="otherCheck"> Other`;
    concernsContainer.appendChild(otherLabel);

    const otherInput = document.createElement("input");
    otherInput.type = "text";
    otherInput.id = "otherText";
    otherInput.placeholder = "Enter your concern";
    otherInput.style.display = "none";
    concernsContainer.appendChild(otherInput);

    const otherCheck = document.getElementById("otherCheck");
    otherCheck?.addEventListener("change", () => {
        otherInput.style.display = otherCheck.checked ? "block" : "none";
    });

    concernForm.addEventListener("submit", (e) => {
        e.preventDefault();

        const enteredName = document.getElementById("name")?.value.trim();
        const age = parseInt(document.getElementById("age")?.value.trim(), 10);
        const displayName = enteredName || currentUser.name;

        if (!displayName || isNaN(age) || age < 16 || age > 75) {
            alert("Please enter valid name and age (16–75).");
            return;
        }

        const selected = Array.from(
            concernsContainer.querySelectorAll("input[type=checkbox]:checked")
        )
            .filter(cb => cb.id !== "otherCheck")
            .map(cb => cb.value);

        if (otherCheck.checked && otherInput.value.trim()) {
            selected.push(otherInput.value.trim());
        }

        if (selected.length === 0) {
            alert("Select at least one concern.");
            return;
        }

        const btn = concernForm.querySelector("button");
        btn.disabled = true;

        resultDiv.style.display = "block";
        resultDiv.innerHTML = "<div class='loading'>Generating your routine...</div>";

        setTimeout(() => {

            let output = `Hello ${displayName}! Here’s your ${category} routine (Age ${age}):\n\n`;

            selected.forEach(concern => {
                const t = routineTemplates[category]?.[concern] || {
                    symptoms: "Varies",
                    causes: "Multiple factors",
                    routine: "Cleanse, moisturize, avoid irritants"
                };

                output += `🔹 ${concern}
• Symptoms: ${t.symptoms}
• Causes: ${t.causes}
• Routine:
   ${t.routine.replace(/\n/g, "\n   ")}\n\n`;
            });

            output += `💡 Tips:
- Stay hydrated
- Be consistent
- Adjust based on results

⚠️ This was AI-generated. Consult a professional if needed.`;
            resultDiv.innerHTML = `<pre style="white-space: pre-wrap;">${output}</pre>`;
            btn.disabled = false;
            resultDiv.scrollIntoView({ behavior: "smooth" });
            if (feedbackSection) feedbackSection.style.display = "block";
        }, 800);
    });

    document.getElementById("submitFeedback")?.addEventListener("click", () => {

        const rating = parseInt(document.getElementById("feedbackRating").value || "0", 10);
        const message = document.getElementById("feedbackMessage").value.trim();

        if (!rating || rating < 1 || rating > 5) {
            alert("Select a valid rating.");
            return;
        }

        saveRoutineFeedback({
            name: currentUser.name,
            userId: currentUser.id,
            role: currentUser.role,
            rating,
            message,
            routineType: category
        });

        document.getElementById("feedbackSection").innerHTML = `
            <div class="success-box">
                <h3>💜 Thank You!</h3>
                <p>Feedback saved successfully.</p>
                <p><strong>Rating:</strong> ${rating}/5</p>
                ${message ? `<p><strong>Message:</strong> ${escapeHTML(message)}</p>` : ""}
            </div>
        `;

        setTimeout(() => {
            window.location.href = "/feedback";
        }, 1800);
    });

    function saveRoutineFeedback({ name, userId, role, rating, message, routineType }) {

        const feedbacks = JSON.parse(localStorage.getItem("feedbacks")) || [];

        feedbacks.push({
            id: crypto.randomUUID(),
            userId,
            name,
            role,
            category: "routine",
            routineType,
            rating,
            message: message || `Routine feedback (${routineType})`,
            createdAt: new Date().toISOString()
        });

        localStorage.setItem("feedbacks", JSON.stringify(feedbacks));
    }

    function escapeHTML(str) {
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;");
    }

});

function cancelForm() {
    if (confirm("Cancel? All data will be lost.")) {
        window.location.href = "/customer-dashboard";
    }
}