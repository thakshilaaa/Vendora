document.addEventListener('DOMContentLoaded', () => {

    const forms = document.querySelectorAll('form[data-validate]');
    forms.forEach(form => {
        form.setAttribute('novalidate', true);

        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            if (validateForm(form)) {
                await handleFormSubmission(form);
            }
        });

        form.querySelectorAll('input, textarea, select').forEach(input => {
            input.addEventListener('blur', () => validateField(input));
            input.addEventListener('input', () => {
                if (input.classList.contains('is-invalid')) {
                    validateField(input);
                }
            });
        });
    });

    function validateField(input) {
        if (!input) return true;

        const value = input.value.trim();
        const feedback = input.parentElement?.querySelector('.invalid-feedback');

        let isValid = true;
        let errorMessage = "";

        if (input.hasAttribute('required') && !value) {
            isValid = false;
            errorMessage = "This field is required.";
        }

        else if (input.type === 'email' && value &&
            !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
            isValid = false;
            errorMessage = "Please enter a valid email address.";
        }

        else if (input.dataset.minLength && value.length < input.dataset.minLength) {
            isValid = false;
            errorMessage = `Must be at least ${input.dataset.minLength} characters.`;
        }

        else if (input.dataset.match) {
            const target = document.getElementById(input.dataset.match);
            if (target && value !== target.value) {
                isValid = false;
                errorMessage = "Passwords do not match.";
            }
        }

        if (!isValid) {
            input.classList.add('is-invalid');
            input.classList.remove('is-valid');
            if (feedback) {
                feedback.textContent = errorMessage;
            }
        } else {
            input.classList.remove('is-invalid');
            input.classList.add('is-valid');
        }
        return isValid;
    }

    function validateForm(form) {
        let isFormValid = true;
        form.querySelectorAll('input, textarea, select').forEach(input => {
            if (!validateField(input)) {
                isFormValid = false;
            }
        });
        return isFormValid;
    }

    async function handleFormSubmission(form) {

        const submitBtn = form.querySelector('[type="submit"]');
        const originalText = submitBtn?.innerHTML;
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = 'Processing...';
        }

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        try {
            const response = await fetch(form.action || "#", {
                method: form.method || 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            if (response.ok) {
                showToast('Success! Form submitted.', 'success');
                form.reset();
                form.querySelectorAll('.is-valid').forEach(el => el.classList.remove('is-valid'));
            } else {
                throw new Error();
            }
        } catch {
            showToast('Something went wrong.', 'error');
        }
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
        }
    }

    document.querySelectorAll('.password-toggle').forEach(btn => {
        btn.addEventListener('click', () => {
            const input = btn.closest('.input-group')?.querySelector('input') || btn.previousElementSibling;
            if (!input) return;
            if (input.type === 'password') {
                input.type = 'text';
                btn.textContent = 'Hide';
            } else {
                input.type = 'password';
                btn.textContent = 'Show';
            }
        });
    });

    document.querySelectorAll('[data-next-step]').forEach(btn => {
        btn.addEventListener('click', () => {
            const currentStep = btn.closest('.form-step');
            if (!currentStep) return;
            let valid = true;
            currentStep.querySelectorAll('input, select, textarea').forEach(input => {
                if (!validateField(input)) valid = false;
            });

            if (valid) {
                currentStep.classList.remove('active');
                const nextStep = document.getElementById(btn.dataset.nextStep);
                if (nextStep) {
                    nextStep.classList.add('active');
                    updateStepIndicators(btn.dataset.nextStep);
                }
            }
        });
    });

    function updateStepIndicators(stepId) {
        const stepNum = parseInt(stepId.replace(/\D/g, '')) || 1;
        document.querySelectorAll('.step-indicator').forEach((ind, index) => {
            ind.classList.remove('active', 'completed');
            if (index + 1 < stepNum) ind.classList.add('completed');
            if (index + 1 === stepNum) ind.classList.add('active');
        });
    }

    function showToast(message, type) {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        const container = document.querySelector('.toast-container') || createToastContainer();
        container.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 500);
        }, 3000);
    }

    function createToastContainer() {
        const container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
        return container;
    }

});