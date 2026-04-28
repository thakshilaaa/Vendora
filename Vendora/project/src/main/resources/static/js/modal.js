class Modal {
    constructor() {
        this.activeModal = null;
        this.scrollPosition = 0;
        this.init();
    }

    init() {
        document.addEventListener('click', (e) => {
            const trigger = e.target.closest('[data-modal-open]');
            if (trigger) {
                e.preventDefault();
                const modalId = trigger.getAttribute('data-modal-open');
                this.open(modalId);
            }

            if (e.target.matches('.modal-overlay') || e.target.closest('[data-modal-close]')) {
                this.close();
            }
        });

        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.activeModal) {
                this.close();
            }
        });
    }

    open(modalId) {
        const modal = document.getElementById(modalId);
        if (!modal) return;
        this.activeModal = modal;
        this.scrollPosition = window.pageYOffset;
        document.body.style.overflow = 'hidden';
        document.body.style.position = 'fixed';
        document.body.style.top = `-${this.scrollPosition}px`;
        document.body.style.width = '100%';

        modal.classList.add('active');
        const overlay = modal.closest('.modal-overlay');
        if (overlay) overlay.classList.add('active');

        const focusable = modal.querySelectorAll('button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])');
        if (focusable.length) setTimeout(() => focusable[0].focus(), 100);
        modal.dispatchEvent(new CustomEvent('modal:open', { detail: { modalId } }));
    }

    close() {
        if (!this.activeModal) return;

        const modal = this.activeModal;
        const overlay = modal.closest('.modal-overlay');

        modal.classList.remove('active');
        if (overlay) overlay.classList.remove('active');

        document.body.style.removeProperty('overflow');
        document.body.style.removeProperty('position');
        document.body.style.removeProperty('top');
        document.body.style.removeProperty('width');
        window.scrollTo(0, this.scrollPosition);

        modal.dispatchEvent(new CustomEvent('modal:close', { detail: { modalId: modal.id } }));
        this.activeModal = null;
    }

    updateContent(modalId, html) {
        const modal = document.getElementById(modalId);
        if (modal) {
            const contentBody = modal.querySelector('.modal-body');
            if (contentBody) contentBody.innerHTML = html;
        }
    }
}

window.AppModal = new Modal();