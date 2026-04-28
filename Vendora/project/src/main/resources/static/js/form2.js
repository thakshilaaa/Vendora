// Form data: delivery, supplier, and routine form inputs, validations, submissions
document.addEventListener("DOMContentLoaded", () => {

    /* Category Toggle (Supplier Products) */
    const categoryCheckboxes = document.querySelectorAll('input[name="productCategories[]"]');
    const productSections = document.querySelectorAll('.form-grid .routine');
    const updateCategorySections = () => {
        productSections.forEach(section => {
            const category = section.dataset.category;
            const isChecked = document.querySelector(`input[name="productCategories[]"][value="${category}"]`)?.checked;
            section.style.display = isChecked ? 'block' : 'none';
            const textarea = section.querySelector('textarea');
            if (textarea) textarea.required = isChecked;
        });
    };
    categoryCheckboxes.forEach(cb => cb.addEventListener('change', updateCategorySections));
    updateCategorySections();

    /* Vehicle Selection */
    const vehicleSelect = document.getElementById("vehicleFilter");
    const vehicleSections = {
        bike: document.querySelector(".bike"),
        car: document.querySelector(".car"),
        van: document.querySelector(".van")
    };
    const vehicleInputs = {
        bike: ["bikeModel", "bikePlate"],
        car: ["carModel", "carPlate"],
        van: ["vanModel", "vanPlate"]
    };
    const updateVehicleSection = () => {
        const selected = vehicleSelect?.value;
        Object.keys(vehicleSections).forEach(type => {
            const sec = vehicleSections[type];
            if (!sec) return;
            sec.style.display = (type === selected) ? 'block' : 'none';
            vehicleInputs[type].forEach(id => {
                const input = document.getElementById(id);
                if (input) {
                    input.required = (type === selected);
                    if (type !== selected) input.value = "";
                }
            });
        });
    };
    vehicleSelect?.addEventListener("change", updateVehicleSection);
    updateVehicleSection(); // initialize

    /* Province → District Logic */
    const province = document.getElementById("province");
    const district = document.getElementById("district");
    const districts = {
        western: ["Colombo", "Gampaha", "Kalutara"],
        central: ["Kandy", "Matale", "Nuwara Eliya"],
        southern: ["Galle", "Matara", "Hambantota"],
        eastern: ["Ampara", "Batticaloa", "Trincomalee"],
        northCentral: ["Anuradhapura", "Polonnaruwa"],
        northern: ["Jaffna", "Kilinochchi", "Mannar", "Mullaitivu", "Vavuniya"],
        northWestern: ["Kurunegala", "Puttalam"],
        uva: ["Badulla", "Moneragala"],
        sabaragamuwa: ["Ratnapura", "Kegalle"]
    };

    province?.addEventListener("change", () => {
        if (!district) return;
        district.innerHTML = '<option value="">Select District</option>';
        (districts[province.value] || []).forEach(d => {
            const opt = document.createElement("option");
            opt.value = d;
            opt.textContent = d;
            district.appendChild(opt);
        });
    });

    /* Form Submission & Validations */
    // supplierForm posts via fetch to /api/partnership/apply; do not hijack with fake success / redirect
    const formIds = ["deliveryForm", "registrationForm"];
    formIds.forEach(formId => {
        const form = document.getElementById(formId);
        if (!form) return;
        form.addEventListener("submit", e => {
            e.preventDefault();
            const phone = form.querySelector("#phone")?.value.trim();       // Phone
            const emergencyPhone = form.querySelector("#emergencyPhone")?.value.trim();
            if (phone && emergencyPhone && phone === emergencyPhone) {
                alert("Emergency phone number cannot be the same as your phone number.");
                return;
            }

            const profileFile = form.querySelector("#profilePicture")?.files[0];    // Profile Picture
            if (profileFile && profileFile.size > 2 * 1024 * 1024) {
                alert("Profile picture must be less than 2MB.");
                return;
            }

            if (categoryCheckboxes.length) {        // Supplier Product Category
                const selected = Array.from(categoryCheckboxes).some(cb => cb.checked);
                if (!selected) {
                    alert("Please select at least one product category.");
                    return;
                }
                for (let sec of productSections) {
                    const textarea = sec.querySelector("textarea");
                    if (sec.style.display !== "none" && textarea && textarea.value.trim() === "") {
                        alert(`Please enter product details for ${textarea.id.replace("Products", "")}.`);
                        return;
                    }
                }
            }

            const visibleVehicle = Object.values(vehicleSections).find(sec => sec?.style.display === "block");  // Vehicle Inputs Validation
            if (visibleVehicle) {
                const inputs = visibleVehicle.querySelectorAll("input[required]");
                for (let input of inputs) {
                    if (input.value.trim() === "") {
                        const labelText = input.previousElementSibling?.textContent || input.name;
                        alert(`Please fill ${labelText}.`);
                        return;
                    }
                }
            }

            alert("Registration submitted successfully!");      // Success
            window.location.href = "../../templates/epic1/dashboard/customer-dashboard.html";
        });
    });
});

/* Global Cancel Function */
function cancelForm() {
    if (confirm("Cancel registration? All entered data will be lost.")) {
        window.location.href = "../../templates/epic1/dashboard/customer-dashboard.html";
    }
}