class Carousel {
    constructor(element, options = {}) {
        this.carousel = element;
        this.track = element.querySelector('.carousel-track');
        this.slides = Array.from(this.track.children);
        this.nextButton = element.querySelector('.carousel-next');
        this.prevButton = element.querySelector('.carousel-prev');
        this.dotsNav = element.querySelector('.carousel-dots');

        this.options = {
            autoPlay: options.autoPlay || false,
            interval: options.interval || 5000,
            loop: options.loop || true,
            ...options
        };

        this.currentIndex = 0;
        this.init();
    }

    init() {
        if (this.dotsNav) {
            this.slides.forEach((_, i) => {
                const dot = document.createElement('button');
                dot.classList.add('carousel-dot');
                if (i === 0) dot.classList.add('active');
                this.dotsNav.appendChild(dot);
            });
            this.dots = Array.from(this.dotsNav.children);
        }

        this.nextButton?.addEventListener('click', () => this.moveToNext());
        this.prevButton?.addEventListener('click', () => this.moveToPrev());

        this.dots?.forEach((dot, i) => {
            dot.addEventListener('click', () => this.goToSlide(i));
        });
        if (this.options.autoPlay) {
            this.startAutoPlay();
            this.carousel.addEventListener('mouseenter', () => this.stopAutoPlay());
            this.carousel.addEventListener('mouseleave', () => this.startAutoPlay());
        }
        this.updateSlidePosition();
    }

    updateSlidePosition() {
        const slideWidth = this.slides[0].getBoundingClientRect().width;
        this.track.style.transform = `translateX(-${this.currentIndex * slideWidth}px)`;
        this.dots?.forEach((dot, i) => {
            dot.classList.toggle('active', i === this.currentIndex);
        });
        if (!this.options.loop) {
            if (this.prevButton) this.prevButton.disabled = this.currentIndex === 0;
            if (this.nextButton) this.nextButton.disabled = this.currentIndex === this.slides.length - 1;
        }
    }

    moveToNext() {
        if (this.currentIndex < this.slides.length - 1) {
            this.currentIndex++;
        } else if (this.options.loop) {
            this.currentIndex = 0;
        }
        this.updateSlidePosition();
    }

    moveToPrev() {
        if (this.currentIndex > 0) {
            this.currentIndex--;
        } else if (this.options.loop) {
            this.currentIndex = this.slides.length - 1;
        }
        this.updateSlidePosition();
    }

    goToSlide(index) {
        this.currentIndex = index;
        this.updateSlidePosition();
    }
    startAutoPlay() {
        this.autoPlayTimer = setInterval(() => this.moveToNext(), this.options.interval);
    }
    stopAutoPlay() {
        clearInterval(this.autoPlayTimer);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const carouselElements = document.querySelectorAll('.carousel');
    carouselElements.forEach(el => {
        new Carousel(el, {
            autoPlay: el.dataset.autoplay === 'true',
            interval: parseInt(el.dataset.interval) || 5000
        });
    });
});